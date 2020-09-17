# Image Vectorquantizer
designed for Proseminar Datenkompression SS20 at RWTH Aachen University
 
 ## General info
 IQCv2 takes a .jpg image and treats the individual RGB values of each Pixel in the image as a threedimensional vector. During encoding, a variation of the Linde-Buzo-Gray Algorithm is used to produce a codebook of a size that can be specified by the user. 
 Supposing a naive image format, that stores each pixel color as three individual values between 0 and 256 (this would result in a codebook with 16.777.216 vectors and 24 bit for each pixel, if each value was stored in binary), this method of compression is capable of a significant saving of storage data.
 Actually, depending on the specific image, using the Linde-Buzo-Gray (LBG) Algorithm, as little as 128 codevectors can be enough to reconstruct an image that is basically indifferentiable to a human observer, as you can see below. 
 
 ![Original image](https://github.com/simonmichau/IQCv2/blob/master/Image%20Results/in.jpg?raw=true "Original Image")
 ![8 codevectors](https://github.com/simonmichau/IQCv2/blob/master/Image%20Results/out8.jpg?raw=true "Original Image")
 ![64 codevectors](https://github.com/simonmichau/IQCv2/blob/master/Image%20Results/out64.jpg?raw=true "Original Image")
 ![512 codevectors](https://github.com/simonmichau/IQCv2/blob/master/Image%20Results/out512.jpg?raw=true "Original Image")
 
 The top left picture is the original one and the following are reconstructions from codebooks with 8, 64 and 512 codevectors.
 The encoding process will result in the files codebook.txt, where the binary values of the algorithmically determined codevectors are stored, and the file transmission.txt, where the binary index of the codevector that corresponds with each pixel is stored.
 During decoding, first the codebook is read, and then each value from the transmission.txt file is matched up with their respective index from the codebook, which is then used to reconstruct the image.
 
 ## Complexity 
 | Space Complexity                                                                                                 | File                 |
 |:---------------------------------------------------------------------------------------------------------------- |:---------------------|
 | ![equation](https://latex.codecogs.com/gif.latex?O%28n%29)                                                       | for codebook.txt      | 
 | ![equation](https://latex.codecogs.com/gif.latex?O%28Height%20%5Ccdot%20Width%20%5Ccdot%20%5Clog_2%28n%29%29%29) | for transmission.txt | 
 
 where ![equation](https://latex.codecogs.com/gif.latex?n) is the number of codevectors.
  
 | Runtime Complexity                                                                              |              |
 |:----------------------------------------------------------------------------------------------- |:-------------|
 | ![equation](https://latex.codecogs.com/gif.latex?O%28Height%20%5Ccdot%20Width%20%5Ccdot%20n%29) | for Encoding | 
 | ![equation](https://latex.codecogs.com/gif.latex?O%28Height%20%5Ccdot%20Width%29)               | for Decoding | 
 
 ## Sources
The picture examples from this readme.md are outputs of this project for pictures licensed under Creative Commons (https://search.creativecommons.org/photos/9e65a3ae-8ea0-4637-8cb4-6d996624f96f)

