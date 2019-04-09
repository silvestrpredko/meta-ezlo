#!/bin/sh

get_input_gpio_value() {
    local gpio_num=$1
    echo "$gpio_num" > /sys/class/gpio/export
    echo "in" > /sys/class/gpio/gpio"${gpio_num}"/direction
    cat /sys/class/gpio/gpio"${gpio_num}"/value
}

release_input_gpio_value() {
    local gpio_num=$1
    echo "$gpio_num" > /sys/class/gpio/unexport
}

case "$1" in
  start)
    echo "check if we are running on master"
    # input mode of GPIOs PA08 and PA09 specify master or slave
    # if both are zero it's master otherwise (10 or 01 or 11)
    # specify slaves

    PA08_VALUE="$(get_input_gpio_value 8)"
    PA09_VALUE="$(get_input_gpio_value 9)"

    if [ "$PA08_VALUE" == "0" ] && [ "$PA09_VALUE" == "0" ]
    then
        echo "applying ksz9897 workarounds"
        /etc/ksz9787_revA1_fixup/ksz9897r-rev.A1-fixup.py &
    fi

    $(release_input_gpio_value 8)
    $(release_input_gpio_value 9)
  ;;

  *)
    echo "Usage: $0 {start}" >&2
    exit 1
  ;;
esac

exit 0
