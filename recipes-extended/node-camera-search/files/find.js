const WebSocket = require('ws');
//const ReconnectingWebSocket = require('reconnecting-websocket');
const net = require('net');

var g_wsClient;
var g_port = 6969;

var fs = require( "fs" );
var path = require( "path" );
var strStartupCWD = path.resolve( process.cwd(), "." );
var strNeededWD = path.resolve( __dirname, "." );
	if( strNeededWD !=  strStartupCWD ) {
		//console.log( "Current startup CWD \"" + strStartupCWD + "\" is not what we need." );
		//console.log( "Will change CWD to  \"" + strNeededWD + "\"." );
		process.chdir( strNeededWD );
	}

var g_Cameras = [];

var child_process = require( "child_process" );
var url = require( "url" );
var cc = require( "./cc" );
var g_strGenericFolderPrefixForGeneratedFiles = "" + __dirname + "/"; // "./";
var g_strFileLock = g_strGenericFolderPrefixForGeneratedFiles + "lock-file";
function lock_file( isON ) {
	if( isON )
		child_process.execSync( "touch " + g_strFileLock );
	else
		child_process.execSync( "rm --force " + g_strFileLock );
}
function sig_handler() {
	handle_shutdown();
}
	process.on( "cleanup", sig_handler );
	process.on( "exit", sig_handler );
	process.on( "SIGTERM", sig_handler );
	process.on( "uncaughtException", sig_handler );
function handle_shutdown() {
	lock_file( false );
	process.exit();
}
	lock_file( true );
var g_strGenericCameraNamePartR = "camera"; // 'R' means removable
var g_strGenericCameraNamePart  = "camera" + "-" + cc.guid() + "-"; // "camera"
var g_strSingleJsonFile               = g_strGenericFolderPrefixForGeneratedFiles + "search-results-json";
var g_strFileNetworkIPsARP            = g_strGenericFolderPrefixForGeneratedFiles + "results-network-arp";
var g_strFileNetworkIPsJSON           = g_strGenericFolderPrefixForGeneratedFiles + "results-network-json";
var g_strPictureFilePrefixR           = g_strGenericFolderPrefixForGeneratedFiles + g_strGenericCameraNamePartR;
var g_strPictureFilePrefix            = g_strGenericFolderPrefixForGeneratedFiles + g_strGenericCameraNamePart;
var g_strPictureFileSuffix            = ".jpg";
var g_strCameraCountFile              = g_strGenericFolderPrefixForGeneratedFiles + "cameras-count";
var g_strCameraVideoUrlFilePrefix     = g_strGenericFolderPrefixForGeneratedFiles + g_strGenericCameraNamePart;
var g_strCameraVideoUrlFileSuffix     = "-url-video";
var g_strCameraSnapshotUrlFilePrefix  = g_strGenericFolderPrefixForGeneratedFiles + g_strGenericCameraNamePart;
var g_strCameraSnapshotUrlFileSuffix  = "-url-snapshot";
var g_strCameraJsonFilePrefix0        = g_strGenericFolderPrefixForGeneratedFiles + g_strGenericCameraNamePart;
var g_strCameraJsonFileSuffix0        = "-json-comprehansive";
var g_strCameraJsonFilePrefix1        = g_strGenericFolderPrefixForGeneratedFiles + g_strGenericCameraNamePart;
var g_strCameraJsonFileSuffix1        = "-json-detailed";
var g_nWorkTimeoutSecondsDefault = 8; // 5
var g_nWorkTimeoutSeconds = 0 + g_nWorkTimeoutSecondsDefault;
var g_lfVersion = 1.1; // version is float
var isVerbose = true;
var isDetailedFiles = false;
var isSingleFileJSON = false;
var isDownloadImages = false;
//var dimensionsMax = { "width": -1, "height": -1 };  // { "width": 160, "height": 120 }; // max image dimensions, do not scale images if negative
//var dimensionsMaxAllowed = { "width": 8192, "height": 8192 }; // max image dimensions allowed to specify from command line
var strDiscoveryMode = "auto"; // auto, none, udp, arp
var isCleanup = true;
var isRoot = ( process.getuid() == 0 ) ? true : false;

if( isVerbose )
		cc.log( "Loading node nodules..." );
var onvif = require( "node-onvif" ); // https://github.com/futomi/node-onvif
//var http = require( "http" );
var request = require( "request" );
	if( isVerbose )
		cc.log( "Done loading node nodules." );

var g_mapCameraDescriptions = {}; // urn -> objCamera
var g_arrComprehansiveResult = [];

function download( uri, filename, callback ) {
	request.head( uri, function( err, res, body ) {
			//cc.log( "content-type:", res.headers[ "content-type" ] );
			//cc.log( "content-length:", res.headers[ "content-length" ] );
			request( uri ).pipe( fs.createWriteStream( filename ) ).on( "close", callback );
		} );
}

function onDeviceInit( strAddr, device, objCamera, objError ) {
	if( objError ) {
		delete g_mapCameraDescriptions[ objCamera.infoPublic.urn ];
		//if( isVerbose )
		//	cc.log( cc.colors.error("Got error camera URL "), strAddr, cc.colors.error(": "), objError );
		return;
	}
	//cc.log( "onDeviceInit for ", strAddr );
	var objInfoPrivateMap = {
		"UdpStreamUrl": null,
		"hwInfo": null,
		"profileCurrent": null,
		"arrProfiles": null,
		"arrUsers": null,
		};
	objCamera.mapInfoPrivateByOnvifAddr[ strAddr ] = objInfoPrivateMap;
	//cc.log( "successful onDeviceInit for ", strAddr );
	objInfoPrivateMap.UdpStreamUrl = device.getUdpStreamUrl();
	objInfoPrivateMap.hwInfo = device.getInformation();
	objInfoPrivateMap.profileCurrent = device.getCurrentProfile();
	objInfoPrivateMap.arrProfiles = device.getProfileList();
	device.services.device.getUsers( ( e, obj ) => {
			if( ! e ) {
				try {
//cc.log( JSON.stringify( obj["data"]["GetUsersResponse"]["User"], null, 4 ) );
					objInfoPrivateMap.arrUsers = obj["data"]["GetUsersResponse"]["User"];
				} catch( e ) {
				}
			}
		});
	g_mapCameraDescriptions[ objCamera.infoPublic.urn ] = objCamera;
	objCamera.onRegistered();
}

function onCameraDiscovered( objInfoPublic ) {
var objCamera = {
		"isSuccessfulDiscovery": false,
		"objComprehansive": {
			"num": null
			, "effectiveStream": null
			, "preferredStream": null
			, "preferredSnapshot": null
			, "hwInfo": null
			, "picFile": null
		//	, "picData": null
			, "mapURLs": {
				"rtsp": {}
				, "udp": {}
				, "http": {}
			}
		}, "infoPublic": objInfoPublic
		, "mapInfoPrivateByOnvifAddr": { } // objInfoPublic.xaddrs[i] -> { ... }
		, "onWriteFiles": function() {
			if( ! isDetailedFiles )
				return;
			try {
				fs.writeFileSync( g_strCameraJsonFilePrefix0 + this.objComprehansive.num + g_strCameraJsonFileSuffix0, JSON.stringify(this.objComprehansive,null,4), "utf8" );
			} catch( e ) {
			}
			try {
				fs.writeFileSync( g_strCameraJsonFilePrefix1 + this.objComprehansive.num + g_strCameraJsonFileSuffix1, JSON.stringify(this,null,4), "utf8" );
			} catch( e ) {
			}
			if( this.objComprehansive.preferredSnapshot ) {
				try {
					fs.writeFileSync( g_strCameraSnapshotUrlFilePrefix + this.objComprehansive.num + g_strCameraSnapshotUrlFileSuffix, "" + this.objComprehansive.preferredSnapshot, "utf8" );
				} catch( e ) {
				}
			}
			var strVideoStream = this.objComprehansive.effectiveStream || this.objComprehansive.preferredStream;
			if( this.objComprehansive.effectiveStream || this.objComprehansive.preferredStream ) {
				try {
					fs.writeFileSync( g_strCameraVideoUrlFilePrefix+ this.objComprehansive.num + g_strCameraVideoUrlFileSuffix, "" + strVideoStream, "utf8" );
				} catch( e ) {
				}
			}
		} , "onRegistered": function () {
			//cc.log( "onRegistered for ", this.infoPublic.urn );
			this.onAppendToGlobalStats();
			g_arrComprehansiveResult.push( this.objComprehansive );
			cc.log( "Camera found ", cc.singleItem( this.infoPublic.xaddrs ), " ", this.objComprehansive );
			g_Cameras.push(this.objComprehansive);
			//cc.log( "pushed to comprehansive result ", this.infoPublic.urn );
			// images downloaded here
			var strPic = this.objComprehansive.picFile;

			// files written at final stage
			//this.onWriteFiles();
			this.isSuccessfulDiscovery = true;
			console.log("Finished this camera discovery!");
		}, "onAppendToGlobalStats": function () {
			//cc.log( "onAppendToGlobalStats for ", this.infoPublic.urn );
			this.objComprehansive.num = g_arrComprehansiveResult.length;
			this.objComprehansive.picFile = g_strPictureFilePrefix + this.objComprehansive.num + g_strPictureFileSuffix;
//
			var urn = this.infoPublic.urn;
			var arrOnvifKeys = Object.keys( this.mapInfoPrivateByOnvifAddr );
			var idxOnvifKey, cntOnvifKeys = arrOnvifKeys.length;
			for( idxOnvifKey = 0; idxOnvifKey < cntOnvifKeys; ++idxOnvifKey ) {
				var strAddr = arrOnvifKeys[ idxOnvifKey ];
				var objInfoPrivateMap = this.mapInfoPrivateByOnvifAddr[ strAddr ];
				try {
					if( ! this.objComprehansive.hwInfo ) {
						this.objComprehansive.hwInfo = objInfoPrivateMap.hwInfo;
					}
				} catch( e ) {
					//this.objComprehansive.hwInfo = {};
				}
				var idxProfile, cntProfiles = objInfoPrivateMap.arrProfiles.length;
				for( idxProfile = 0; idxProfile < cntProfiles; ++idxProfile ) {
					var prf = objInfoPrivateMap.arrProfiles[ idxProfile ];
					if( prf.snapshot && prf.snapshot.length > 0 ) {
						this.objComprehansive.preferredStream = "" + objInfoPrivateMap.UdpStreamUrl;
						this.objComprehansive.mapURLs["rtsp"][ objInfoPrivateMap.UdpStreamUrl ] = "preferred";
					}
					if( prf.stream ) {
						if( prf.stream.udp && prf.stream.udp.length > 0 ) {
							this.objComprehansive.mapURLs["udp"][ prf.stream.udp ] = "udp, profile";
						}
						if( prf.stream.http && prf.stream.http.length > 0 ) {
							this.objComprehansive.mapURLs["http"][ prf.stream.http ] = "http, profile";
						}
						if( prf.stream.rtsp && prf.stream.rtsp.length > 0 ) {
							this.objComprehansive.mapURLs["rtsp"][ prf.stream.rtsp ] = "rtsp, profile";
						}
					}
				}
				if( objInfoPrivateMap.stream ) {
					if( objInfoPrivateMap.stream.udp && objInfoPrivateMap.stream.udp.length > 0 ) {
						this.objComprehansive.mapURLs["udp"][ objInfoPrivateMap.stream.udp ] = "udp, current profile";
					}
					if( objInfoPrivateMap.stream.http && objInfoPrivateMap.stream.http.length > 0 ) {
						this.objComprehansive.mapURLs["http"][ objInfoPrivateMap.stream.http ] = "http, current profile";
					}
					if( objInfoPrivateMap.stream.rtsp && objInfoPrivateMap.stream.rtsp.length > 0 ) {
						this.objComprehansive.mapURLs["rtsp"][ objInfoPrivateMap.stream.rtsp ] = "rtsp, current profile";
					}
				}
				if( objInfoPrivateMap.profileCurrent.snapshot && objInfoPrivateMap.profileCurrent.snapshot.length > 0 ) {
					this.objComprehansive.mapURLs["http"][ objInfoPrivateMap.profileCurrent.snapshot ] = "current profile";
					this.objComprehansive.preferredSnapshot = "" + objInfoPrivateMap.profileCurrent.snapshot;
				}
				if( objInfoPrivateMap.UdpStreamUrl && objInfoPrivateMap.UdpStreamUrl.length > 0 ) {
					this.objComprehansive.mapURLs["rtsp"][ objInfoPrivateMap.UdpStreamUrl ] = "preferred";
				}
			} // for( idxOnvifKey = 0; idxOnvifKey < cntOnvifKeys; ++idxOnvifKey )
			//cc.log( "Append to GlobalStats finished for ", this.infoPublic.urn );
		}
	};
	try {
		var idxAddr, cntAddrs = objInfoPublic.xaddrs.length;
		for( idxAddr = 0; idxAddr < cntAddrs; ++idxAddr ) {
			var strAddr = objInfoPublic.xaddrs[ idxAddr ];
			var device = new onvif.OnvifDevice( {
					"xaddr": "" + strAddr //,
					//"user" : "admin",
					//"pass" : "admin123"
				} );
			device.init( ( objError ) => { onDeviceInit( strAddr, device, objCamera, objError ); } );
		}
	} catch( e ) {
		delete g_mapCameraDescriptions[ objCamera.infoPublic.urn ];
	}
}

function discoverExplicitly( strAddr ) {
var probe = {
		"urn"     : "urn-explicit-pseudo-" + strAddr,
		"name"    : "name-pseudo-" + strAddr,
		"hardware": "",
		"location": "",
		"types"   : [ "dn:NetworkVideoTransmitter" ],
		"xaddrs"  : [ strAddr ],
		"scopes"  : [
			"onvif://www.onvif.org/Profile/Streaming",
			"onvif://www.onvif.org/model/C6F0SeZ0N0P0L0",
			"onvif://www.onvif.org/name/IPCAM" //,
			//"onvif://www.onvif.org/location/country/china"
		]
	};
	onCameraDiscovered( probe );
}

var g_arrLocalNetworkAddresses = [];

function discoverAll() {
	if( isVerbose )
		cc.log( "ARP scanning..." );
//sudo nmap -sP 192.168.88.0/24 | grep "Nmap scan report for "
//sudo arp-scan --interface=eth0 --localnet
//sudo arp-scan --localnet
//sudo arp-scan --localnet --quiet
//sudo arp-scan --localnet| awk '{print $1}'|tail -n +3|head -n -2
	child_process.execSync(
		"arp-scan --localnet| awk '{print $1}'|tail -n +3|head -n -2 > " + g_strFileNetworkIPsARP,
		{ stdio: [ process.stdin, process.stdout, process.stderr ] }
		);
	try {
		g_arrLocalNetworkAddresses = fs.readFileSync( g_strFileNetworkIPsARP, { "encoding": "utf-8" } ).split( "\n" );
	} catch( e ) {
	}
var idxAddr, cntAddrs = g_arrLocalNetworkAddresses.length;
	for( idxAddr = 0; idxAddr < cntAddrs; ) {
		var strAddr = "" + g_arrLocalNetworkAddresses[ idxAddr ];
		if( (!strAddr) || strAddr == "" || strAddr == "undefined" || strAddr == "null" ) {
			g_arrLocalNetworkAddresses.splice( idxAddr, 1 );
			-- cntAddrs;
			continue;
		}
		strAddr = strAddr.replace( " ", "" ).replace( "\r", "" ).replace( "\n", "" ).replace( "\t", "" );
		g_arrLocalNetworkAddresses[ idxAddr ] = strAddr;
		if( strAddr == "" ) {
			g_arrLocalNetworkAddresses.splice( idxAddr, 1 );
			-- cntAddrs;
			continue;
		}
		++ idxAddr;
	}
	cntAddrs = g_arrLocalNetworkAddresses.length;
	if( isVerbose )
		cc.log( "Done, ARP scan got ", cntAddrs, " address(es)." );
	try {
		if( isDetailedFiles )
			fs.writeFileSync( g_strFileNetworkIPsJSON, JSON.stringify(g_arrLocalNetworkAddresses,null,4), "utf8" );
		else
			child_process.execSync( "rm --force " + g_strFileNetworkIPsARP );
	} catch( e ) {
	}
	for( idxAddr = 0; idxAddr < cntAddrs; ++idxAddr ) {
		var strAddr = "" + g_arrLocalNetworkAddresses[ idxAddr ];
		discoverExplicitly( "http://" + strAddr + ":8080/onvif/devices" );
		//discoverExplicitly( "http://" + strAddr + ":8090/onvif/devices" );
	}
	console.log("Finished discoverAll");
}

function compute_camera_effective_stream_URLs() {
	if( isVerbose )
		cc.log( "Writing camera effective stream URLs..." );
var ak = Object.keys(g_mapCameraDescriptions);
var idxCamera, cntCameras = ak.length;
	for( idxCamera = 0; idxCamera < cntCameras; ++ idxCamera ) {
		var objCamera = g_mapCameraDescriptions[ ak[ idxCamera ] ];
		var objURL = cc.safeURL( objCamera.objComprehansive.preferredStream );
		if( objURL != null && objURL != undefined ) {
			var strURL = cc.url2str( objURL );
			objCamera.objComprehansive.effectiveStream = strURL;
		}
		if( ! objCamera.objComprehansive.effectiveStream )
			objCamera.objComprehansive.effectiveStream = "" + objCamera.objComprehansive.preferredStream;
		// compose objComprehansive.userName and objComprehansive.password if present
		objCamera.objComprehansive.userName = "";
		objCamera.objComprehansive.password = "";
		try {
			var objURL = url.parse( objCamera.objComprehansive.effectiveStream );
			if( objURL.username && objURL.username.length > 0 ) {
				objCamera.objComprehansive.userName = "" + objURL.username;
				if( objCamera.objComprehansive.userName.length > 0 )
					objCamera.objComprehansive.password = "" + objURL.password;
			}
			if( (!objCamera.objComprehansive.userName) || objCamera.objComprehansive.userName.length == 0 ) {
				// try extract em from preferredSnapshot
				objURL = url.parse( objCamera.objComprehansive.preferredSnapshot );
				var sp = new url.URLSearchParams( objURL.query );
				sp.forEach( ( value, name, sp ) => {
						var strName = cc.replaceAll( name.toLowerCase().trim(), "-", "" );
						if( (!value) || value.length == 0 )
							return true;
						if( (!objCamera.objComprehansive.userName) || objCamera.objComprehansive.userName.length == 0 ) {
							if(		strName == "usr"
								||	strName == "user"
								||	strName == "usrname"
								||	strName == "username"
								||	strName == "login"
								||	strName == "loginname"
								) {
								objCamera.objComprehansive.userName = "" + value;
								return true;
							}
						}
						if( (!objCamera.objComprehansive.password) || objCamera.objComprehansive.password.length == 0 ) {
							if(		strName == "pwd"
								||	strName == "pass"
								||	strName == "passwd"
								||	strName == "password"
								||	strName == "usrpwd"
								||	strName == "userpwd"
								||	strName == "usrpass"
								||	strName == "userpass"
								) {
								objCamera.objComprehansive.password = "" + value;
								return true;
							}
						}
					});
				if( (!objCamera.objComprehansive.userName) || objCamera.objComprehansive.userName.length == 0 )
					objCamera.objComprehansive.password = "";
				else {
					// update effectiveStream
					var objURL = url.parse( objCamera.objComprehansive.effectiveStream );
					if( (!objURL.username) || objURL.username.length == 0 )
						objURL.username = "" + objCamera.objComprehansive.userName;
					if( (!objURL.password) || objURL.password.length == 0 )
						objURL.password = "" + objCamera.objComprehansive.password;
					objCamera.objComprehansive.effectiveStream = "" + cc.url2str( objURL );
				}
			}
		} catch( e ) {
		}
	}
	if( isVerbose )
		cc.log( "Done, finished effective stream URLs." );
}

function write_camera_files() {
	if( (!isDetailedFiles) && (!isSingleFileJSON) )
		return;
var ak = Object.keys(g_mapCameraDescriptions);
var idxCamera, cntCameras = ak.length;
	if( isDetailedFiles ) {
		if( isVerbose )
			cc.log( "Writing camera files..." );
		for( idxCamera = 0; idxCamera < cntCameras; ++ idxCamera ) {
			var objCamera = g_mapCameraDescriptions[ ak[ idxCamera ] ];
			objCamera.onWriteFiles();
		}
		if( isVerbose )
			cc.log( "Done, finished writing camera files." );
	} // if( isDetailedFiles )
	if( isSingleFileJSON ) {
		if( isVerbose )
			cc.log( cc.colors.debug("Writing single result file"), cc.colors.warn(g_strSingleJsonFile), cc.colors.debug(" ...") );
		var objSingle = [];
		for( idxCamera = 0; idxCamera < cntCameras; ++ idxCamera ) {
			var objCamera = g_mapCameraDescriptions[ ak[ idxCamera ] ];
			var objToPush = objCamera.objComprehansive;
			try {
//cc.log( "preparing ", idxCamera, " of ", cntCameras );
				if( "num" in objToPush )
					delete objToPush.num;
				if( "mapURLs" in objToPush )
					delete objToPush.mapURLs;
//cc.log( "pushing ", idxCamera, " of ", cntCameras );
			objSingle.push( objToPush );
			} catch( e ) {
//cc.log( "push exception ", e );
			}
		}
		try {
			fs.writeFileSync( g_strSingleJsonFile, JSON.stringify(objSingle,null,4), "utf8" );
		} catch( e ) {
			if( isVerbose )
				cc.log( cc.colors.error("Error writing single JSON file "), e );
		}
		if( isVerbose )
			cc.log( "Done, finished single result file." );
	} // if( isSingleFileJSON )
}

var g_nRtmpPort = 1935;

function final_tasks() {
	compute_camera_effective_stream_URLs();
	write_camera_files();
	lock_file( false );
	if( isVerbose )
			cc.log( "Done, timeout of ", g_nWorkTimeoutSeconds, " seconds reached, ", cc.colors.rainbow("discovery finished"), "." );
	if( strNeededWD !=  strStartupCWD ) {
		//console.log( "Will restore startup CWD \"" + strStartupCWD + "\"." );
		process.chdir( strStartupCWD );
	}
}

process.on('uncaughtException', function(err) {
	scan_error(err);
});

let [target, timeout, port] = process.argv.slice(2);
var timerknock;

function scan_error(err) {
	console.log("Scan Cameras Error: " + err);
	process.exit(1);
}

function scan_cameras(websocket, target, timeout) {
	g_Cameras = [];
	discoverAll();
	setTimeout(() => {
		send_scan_result(websocket, target);
	}, timeout);
}

function send_scan_result(websocket, target) {
	websocket.send(
		JSON.stringify({
			"target" : target,
			"cameras" : JSON.stringify(g_Cameras)
		})
	);
	timerknock = setTimeout(() => {
		scan_error("Timeout Websocket client");
	}, timeout * 2);
}

if (![target, timeout, port].every(Boolean)) {
	scan_error("Miss parameter");
}

let websocket = new WebSocket("ws://127.0.0.1:" + port.toString());

websocket.onopen = function(evt) {
	scan_cameras(websocket, target, timeout);
};

websocket.onmessage = function(evt) {
	console.log("Receive Response and close");
	clearTimeout(timerknock);
	process.exit(0);
};

websocket.onerror = function(evt) {
	scan_error("Web Socket " + evt.data);
};

websocket.onclose = function(evt) {
	scan_error("WebSocket closed");
};