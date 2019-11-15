#!/bin/bash
helptext="drawArt: drawArt [filename] mode | ColorDiff LineSkip\n\n 
filename\tName of the image.
mode\t256colors, 16colors, grayscale.
ColorDiff, LineSkip\tOnly needed if you don’t have the right image, read the documentation for gen.jar"

if [[ "$1" = "" ]]; then
	echo "Write drawArt -h or drawArt --help for help";
	exit;
elif [[ "$1" = "-h" ]]; then
	echo -e "$helptext";
	exit;
elif [[ "$1" = "--help" ]]; then
	echo -e "$helptext";
	exit;
fi
if ! [ -f "$1" ]; then
	echo "Error. File \"$1\" does not exist.";
	exit 1;
fi
scrdir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
filepath=$(realpath "$1")
filedir=$(dirname "$filepath")
filename=$(basename -- "$1")
fileext="${filename##*.}"
filename="${filename%.*}"
savename="${filename}_$2.txt"
if [ -f "$filedir/$savename" ]; then
	echo -e "$(<"$filedir"/"$savename")";
	exit;
elif ! [ "$3" == "" ]; then
	echo "File $filedir/$savename is absent. Trying to create."
	java -jar "$scrdir/gen.jar" "$2" "$filedir" "$filename" "$fileext" "$3" "$4"
	echo -e "$(<"$filedir"/"$savename")";
	exit;
else
	echo "Error. File $filedir/$savename is absent. Creation attempt failed: arguments missing.";
	exit 1;
fi
