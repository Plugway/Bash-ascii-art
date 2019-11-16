# Bash ascii art

Turn any of your images into bash art.

## Example

![That's cool :)](https://github.com/Plugway/Bash-ascii-art/blob/master/mmfiles/1.jpg)

## How to use it

1. Download [binaries](https://github.com/Plugway/Bash-ascii-art/releases/).
2. Extract it.
3. Read "gen.jar help.txt".
4. Run drawArt.sh with key -h and read.
5. Install Java RE if you have not already done so:  
`sudo apt-get install default-jre`.
6. Put the images in the "images" folder.
7. Run drawArt.sh with the necessary parameters.

## drawArt.sh commands example
### If you do not have a generated image
```
./drawArt.sh ./images/1.jpg grayscale a 3
./drawArt.sh ./images/1.jpg 16colors a 3
./drawArt.sh ./images/1.jpg 256colors a 3
```
### If you already have a generated image
```
./drawArt.sh ./images/1.jpg grayscale
./drawArt.sh ./images/1.jpg 16colors
./drawArt.sh ./images/1.jpg 256colors
```
