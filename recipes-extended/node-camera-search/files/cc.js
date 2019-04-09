var colors = require( "colors/safe" ); // https://www.npmjs.com/package/colors
var url = require( "url" );

colors.setTheme( {
		normal:   "grey",
		silly:    "rainbow",
		input:    "grey",
		verbose:  "cyan",
		prompt:   "grey",
		info:     "green",
		data:     "grey",
		help:     "cyan",
		warn:     "yellow",
		debug:    "grey", // "blue",
		error:    "red",
		success:  "green",
		yes:      "green",
		no:       "red",
		number:   "magenta",
		real:     "magenta",
		nanval:   "red",
		undefval: "blue",
		nullval:  "blue",
		strval:   "yellow",
		cla:      "cyan",
		example:  "blue",
		fspath:   "green",
	} );

function guid() {
	function s4() {
		return Math.floor((1 + Math.random()) * 0x10000)
		.toString(16)
		.substring(1);
	}
	return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
		s4() + '-' + s4() + s4() + s4();
}

function union_arrays( x, y ) {
var obj = {};
	for (var i = x.length-1; i >= 0; -- i)
		obj[x[i]] = x[i];
	for (var i = y.length-1; i >= 0; -- i)
		obj[y[i]] = y[i];
var res = []
	for (var k in obj) {
	if (obj.hasOwnProperty(k))  // <-- optional
		res.push(obj[k]);
	}
	return res;
}

function parseBool( str ) {
	if(str == null)
		return false;
	if( typeof str === "boolean" )
		return ( str === true );

	if( typeof str === "string" ) {
		if( str == "" )
			return false;
		str = str.replace(/^\s+|\s+$/g, '');
		if(str.toLowerCase() == 'true' || str.toLowerCase() == 'yes')
			return true;
		str = str.replace(/,/g, '.');
		str = str.replace(/^\s*\-\s*/g, '-');
	}
	// var isNum = string.match(/^[0-9]+$/) != null;
	// var isNum = /^\d+$/.test(str);
	if( ! isNaN( str ) )
		return ( parseFloat( str ) != 0 );
	return false;
}

function url2str( objURL ) {
var strProtocol = ( objURL.protocol && objURL.protocol.length > 0 ) ? ( "" + objURL.protocol + "//" ) : "";
var strUP = "";
var strHost = ( objURL.hostname && objURL.hostname.length > 0 ) ? ( "" + objURL.hostname.toString() ) : "";
var strPort = objURL.port ? ( ":" + objURL.port ) : "";
var strPath =  ( objURL.pathname && objURL.pathname.length > 0 ) ? ( "" + objURL.pathname ) : "";
var strSearch = ( objURL.search && objURL.search.length > 0 ) ? ( "" + objURL.search ) : "";
//cc.log( "username ", objURL.username );
//cc.log( "password ", objURL.password );
	if( objURL.username && objURL.username.length > 0 ) {
		strUP += "" + objURL.username;
		if( objURL.password && objURL.password.length > 0 )
			strUP += ":" + objURL.password;
		strUP += "@";
	}
//cc.log( "strUP ", strUP );
var strURL = "" + strProtocol + strUP + strHost + strPort + strPath + strSearch;
//cc.log( "composed ", strURL );
	return strURL;
}

function url2strWithoutCredentials( objURL ) {
var strProtocol = ( objURL.protocol && objURL.protocol.length > 0 ) ? ( "" + objURL.protocol + "//" ) : "";
var strUP = "";
var strHost = ( objURL.hostname && objURL.hostname.length > 0 ) ? ( "" + objURL.hostname.toString() ) : "";
var strPort = objURL.port ? ( ":" + objURL.port ) : "";
var strPath =  ( objURL.pathname && objURL.pathname.length > 0 ) ? ( "" + objURL.pathname ) : "";
var strSearch = ( objURL.search && objURL.search.length > 0 ) ? ( "" + objURL.search ) : "";
//cc.log( "username ", objURL.username );
//cc.log( "password ", objURL.password );
	//if( objURL.username && objURL.username.length > 0 ) {
	//	strUP += "" + objURL.username;
	//	if( objURL.password && objURL.password.length > 0 )
	//		strUP += ":" + objURL.password;
	//	strUP += "@";
	//}
//cc.log( "strUP ", strUP );
var strURL = "" + strProtocol + strUP + strHost + strPort + strPath + strSearch;
//cc.log( "composed ", strURL );
	return strURL;
}

function isInt( n ) { return ( Number(n) === n && n % 1 === 0 ) ? false : true; }
function isFloat( n ) { return ( Number(n) === n && n % 1 !== 0 ) ? false : true; }
function isInt2( n ) {
    var intRegex = /^-?\d+$/;
    if( ! intRegex.test( n ) )
        return false;
    var intVal = parseInt( n, 10 );
    return parseFloat( n ) == intVal && !isNaN(intVal);
}
function isFloat2( n ) { var val = parseFloat( n ); return isNaN( val ) ? false : true; }

function singleItem( arg ) {
	if( ! Array.isArray( arg ) )
		return arg;
var retVal = null;
var cnt = arg.length;
	switch( cnt ) {
		case 0: break;
		case 1: retVal = arg[ 0 ]; break;
		default: retVal = arg; break;
	}
	return retVal;
}

function safeURL( arg ) {
	try {
		var sc = arg[0];
		if( sc == "\"" || sc == "'" ) {
			var cnt = arg.length;
			if( arg[cnt-1] == sc ) {
				var ss = arg.substring( 1, cnt-1 );
				var objURL = safeURL( ss );
				if( objURL != null && objURL != undefined )
					objURL.strStrippedStringComma = sc;
				return objURL;
			}
			return null;
		}
		var objURL = url.parse( arg );
		if( ! objURL.hostname )
			return null;
		if( objURL.hostname.length == 0 )
			return null;
		objURL.strStrippedStringComma = null;
		return objURL;
	} catch( e ) {
		return null;
	}
}

function yn( flag ) { return flag ? colors.yes( "yes" ) : colors.no( "no" ) }

/**
 * Traverses a javascript object, and deletes all circular values
 * @param source object to remove circular references from
 * @param censoredMessage optional: what to put instead of censored values
 * @param censorTheseItems should be kept null, used in recursion
 * @returns {undefined}
 */
function preventCircularJson( source, censoredMessage, censorTheseItems ) {
	//init recursive value if this is the first call
	censorTheseItems = censorTheseItems || [source];
	//default if none is specified
	censoredMessage = censoredMessage || "CIRCULAR_REFERENCE_REMOVED";
	//values that have allready apeared will be placed here:
	var recursiveItems = {};
	//initaite a censored clone to return back
	var ret = {};
	//traverse the object:
	for (var key in source) {
		var value = source[key]
		if (typeof value == "object") {
			//re-examine all complex children again later:
			recursiveItems[key] = value;
		} else {
			//simple values copied as is
			ret[key] = value;
		}
	}
	//create list of values to censor:
	var censorChildItems = [];
	for (var key in recursiveItems) {
		var value = source[key];
		//all complex child objects should not apear again in children:
		censorChildItems.push(value);
	}
	//censor all circular values
	for (var key in recursiveItems) {
		var value = source[key];
		var censored = false;
		censorTheseItems.forEach(function (item) {
			if (item === value) {
				censored = true;
			}
		});
		if (censored) {
			//change circular values to this
			value = censoredMessage;
		} else {
			//recursion:
			value = preventCircularJson(value, censoredMessage, censorChildItems.concat(censorTheseItems));
		}
		ret[key] = value

	}
	return ret;
}

var jsonColorizer = { // see http://jsfiddle.net/unLSJ/
		cntCensoredMax: 30000 // zero to disable censoring
		, censor: ( censor ) => {
			var i = 0;
			return ( key, value ) => {
				if( i !== 0 && typeof(censor) === 'object' && typeof(value) == 'object' && censor == value ) 
					return '[Circular]'; 
				if( i >= jsonColorizer.cntCensoredMax )
					return '[Unknown]';
				++i; // so we know we aren't using the original object anymore
				return value;  
			}
		}, replacerHTML: ( match, pIndent, pKey, pVal, pEnd ) => {
			var key = "<span class=json-key>";
			var val = "<span class=json-value>";
			var str = "<span class=json-string>";
			var r = pIndent || "";
			if( pKey )
				r = r + key + pKey.replace( /[": ]/g, "" ) + "</span>: ";
			if( pVal )
				r = r + (pVal[0] == "\"" ? str : val) + pVal + "</span>";
			return r + ( pEnd || "" );
		}, prettyPrintHTML: ( obj ) => {
			var jsonLine = /^( *)("[\w]+": )?("[^"]*"|[\w.+-]*)?([,[{])?$/mg;
			var s =
				JSON.stringify( obj, ( jsonColorizer.cntCensoredMax > 0 ) ? jsonColorizer.censor( obj ) : null, 4 )
				.replace( /&/g, "&amp;").replace(/\\"/g, "&quot;" )
				.replace( /</g, "&lt;").replace(/>/g, "&gt;" )
				.replace( jsonLine, jsonColorizer.replacerHTML )
				;
			return s;
		}, replacerConsole: ( match, pIndent, pKey, pVal, pEnd ) => {
			var r = pIndent || "";
			if( pKey )
				r = r + log_arg_to_str( pKey.replace( /[": ]/g, "" ) ) + ": ";
			if( pVal )
				r = r + log_arg_to_str( pVal );
			return r + ( pEnd || "" );
		}, prettyPrintConsole: ( obj ) => {
			var jsonLine = /^( *)("[\w]+": )?("[^"]*"|[\w.+-]*)?([,[{])?$/mg;
			var s =
				JSON.stringify( obj, ( jsonColorizer.cntCensoredMax > 0 ) ? jsonColorizer.censor( obj ) : null, 4 )
				.replace( jsonLine, jsonColorizer.replacerConsole )
				;
			return s;
		}
	};

function replaceAll( str, find, replace ) {
	return str.replace(new RegExp(find, 'g'), replace);
}

function to_ipv4_arr( s ) {  
	if( /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/.test( s ) ) {
		var arr = s.split( "." );
		if( (!arr) || arr.length != 4 )
			return null;
		return arr;
	}
	return null;
}  
function log_arg_to_str_as_ipv4( arg ) {
	var arr = to_ipv4_arr( arg );
	if( ! arr )
		return arg;
var s = "";
	for( var i = 0; i < 4; ++ i ) {
		if( i > 0 )
			s += colors.normal( "." );
		s += log_arg_to_str( arr[ i ] );
	}
	return s;
}
function log_arg_to_str() {
var i, cnt = arguments.length, s = "";
	for( i = 0; i < cnt; ++i ) {
		var arg = arguments[ i ];
		if( arg === undefined ) {
			s += "" + colors.undefval( arg );
			continue;
		}
		if( arg === null ) {
			s += "" + colors.nullval( arg );
			continue;
		}
		if( typeof arg === "boolean" ) {
			s += "" + yn( arg );
			continue;
		}
		if( typeof arg === "object" && typeof arg.valueOf() === "boolean" ) {
			s += "" + yn( arg.valueOf() );
		}
		if( typeof arg === "number" ) {
			s += "" + colors.number( arg );
			continue;
		}
		if( typeof arg === "object" && typeof arg.valueOf() === "number" ) {
			s += "" + colors.number( arg.valueOf() );
			continue;
		}
		/*if( isNaN( arg ) ) {
			s += "" + colors.nanval( arg );
			continue;
		}*/
		if( typeof arg === "string" || arg instanceof String ) {
			var objURL = safeURL( arg );
			if( objURL != null && objURL != undefined ) {
				var strURL = "";
				if( objURL.strStrippedStringComma )
					strURL += colors.normal(objURL.strStrippedStringComma);
				if( objURL.protocol )
					strURL += "" + colors.yellow( objURL.protocol ) + colors.normal("//");
				if( objURL.username) {
					strURL += "" + colors.magenta( objURL.username );
					if( objURL.password )
						strURL += colors.normal(":") + colors.yellow( objURL.password );
					strURL += colors.normal("@");
				}
				if( objURL.hostname )
					strURL += "" + colors.magenta( log_arg_to_str_as_ipv4( objURL.hostname ) );
				if( objURL.port )
					strURL += colors.normal(":") + log_arg_to_str( objURL.port );
				if( objURL.pathname )
					strURL += "" + colors.yellow( replaceAll( objURL.pathname, "/", colors.normal( "/" ) ) );
				if( objURL.search )
					strURL += "" + colors.magenta( objURL.search );
				if( objURL.strStrippedStringComma )
					strURL += colors.normal(objURL.strStrippedStringComma);
				s += strURL;
				continue;
			}
			if(    ( arg.length > 1 && arg[0] == "-" && arg[1] != "-" )
				|| ( arg.length > 2 && arg[0] == "-" && arg[1] == "-" && arg[2] != "-" )
				) {
				s += "" + colors.cla( arg );
				continue;
			}
			if( arg.length > 0 && ( arg[0] == "\"" || arg[0] == "'" ) ) {
				s += "" + colors.strval( arg );
				continue;
			}
			/*if( isFloat( arg ) ) {
				s += "" + colors.real( arg );
				continue;
			}
			if( isInt( arg ) ) {
				s += "" + colors.number( arg );
				continue;
			}*/
			if( isFloat2( arg ) ) {
				s += "" + colors.real( arg );
				continue;
			}
			if( isInt2( arg ) ) {
				s += "" + colors.number( arg );
				continue;
			}
		}
		if( Array.isArray( arg ) || typeof arg == "object" ) {
			//s += JSON.stringify(arg);
			s += jsonColorizer.prettyPrintConsole( arg );
			continue;
		}
		s += "" + colors.normal( arg );
	}
	return s;
}
function log() {
var i, cnt = arguments.length, s = "";
	for( i = 0; i < cnt; ++i )
		s +=  log_arg_to_str( arguments[ i ] );
	console.log( s );
}

function extractSafeString( obj, argName, defVal ) {
	defVal = "" + ( defVal || "" );
	if( (!obj) || (!argName) )
		return defVal;
var s = defVal;
	try {
		if( argName in obj )
			s = "" + obj[argName];
	} catch( e ) {
		s = defVal;
	}
	return s;
}
function extractSafeInt( obj, argName, defVal ) {
	defVal = 0 + ( defVal || 0 );
	if( (!obj) || (!argName) )
		return defVal;
var n = defVal;
	try {
		var s = extractSafeString( obj, argName, defVal );
		n = parseInt( s );
		if( n == undefined || n == null || n == NaN )
			n = defVal;
	} catch( e ) {
		n = defVal;
	}
	return n;
}
function extractSafeFloat( obj, argName, defVal ) {
	defVal = 0.0 + ( defVal || 0.0 );
	if( (!obj) || (!argName) )
		return defVal;
var n = defVal;
	try {
		var s = extractSafeString( obj, argName, defVal );
		n = parseFloat( s );
		if( n == undefined || n == null || n == NaN )
			n = defVal;
	} catch( e ) {
		n = defVal;
	}
	return n;
}
function extractSafeBool( obj, argName, defVal ) {
	if( defVal == undefined || defVal == null || defVal == NaN )
		defVal = false;
	else
		defVal = ( defVal || false ) ? true : false;
	if( (!obj) || (!argName) )
		return defVal;
var n = defVal;
	try {
		var s = extractSafeString( obj, argName, defVal );
		n = parseBool( s );
		if( n == undefined || n == null || n == NaN )
			n = defVal;
	} catch( e ) {
		n = defVal;
	}
	return n;
}

module.exports = {
	"colors": colors,
	"guid": guid,
	"union_arrays": union_arrays,
	"parseBool": parseBool,
	"url2str": url2str,
	"url2strWithoutCredentials": url2strWithoutCredentials,
	"isInt": isInt,
	"isInt2": isInt,
	"isFloat": isFloat,
	"isFloat2": isFloat,
	"singleItem": singleItem,
	"safeURL": safeURL,
	"yn": yn,
	"preventCircularJson": preventCircularJson,
	"jsonColorizer": jsonColorizer,
	"replaceAll": replaceAll,
	"to_ipv4_arr": to_ipv4_arr,
	"log_arg_to_str_as_ipv4": log_arg_to_str_as_ipv4,
	"log_arg_to_str": log_arg_to_str,
	"log": log,
	"extractSafeString": extractSafeString,
	"extractSafeInt": extractSafeInt,
	"extractSafeFloat": extractSafeFloat,
	"extractSafeBool": extractSafeBool,
	fool_bar: function () {
		// whatever
	}
};
