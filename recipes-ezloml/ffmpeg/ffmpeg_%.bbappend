DEPENDS += "x265 libass fdk-aac libopus"
EXTRA_OECONF += "--enable-gpl \
                 --enable-libass \
                 --enable-libfdk-aac \
                 --enable-libfreetype \
                 --enable-libmp3lame \
                 --enable-libopus \
                 --enable-libtheora \
                 --enable-libvorbis \
                 --enable-libvpx \
                 --enable-libx264 \
                 --enable-libx265 \
                 --enable-shared \
                 --enable-vdpau \
                 --enable-nonfree \
                 --enable-static \
"
