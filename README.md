# ModTanhCeenGUI_Plugin
This is a Imagej plugin which implements an analytical non-linear transformation to locally enhance contrast in digital images.
It is designed to work as a plugin for the ImageJ sofware developed in Java [see](https://imagej.net/ij/).
An article about the mathematical formulation will be avaiable in the future. 

## Installation

1. First download the most recent version of ImageJ from the official [page](https://imagej.net/ij/download.html).
2. Copy the file ModTanhCEEN_RGB_GUI.java into the folder ImageJInstallationPath/ImageJ/plugins.
3. Run ImageJ and in the main tool bar select plugins-> Compile and Run
4. Select the file ModTanhCEEN_RGB_GUI.java and you are good to go.

Note 1: If there is no image open previously the plugin will not run and only give a warning. However after it is compiled it can be accesed from the plugins menu.

Note 2: The plugin only works with RGB images like .png and .jpg

## Usage

ModTanhCeenGUI_Plugin opens two windows:

 - MainWindow with the image and a side panel which allows to localy increase the contrast around a particular pixel intensity region.
 - A plot window which shows the current pixel to pixel transformation applied to it.

For most purposes only the $q_0$ and $\lambda$ parameters are relevant to enhance contrast. The whole process can be simplified as follows:

1. Set $q_0$ equal to a pixel intensity value in the region of interest.
2. Start increasing the $\lambda$ parameter from 0 until the resulting image exhibits the best contrast.
3. (Optional) If the resulting image appears too bright (dark), adjust the $q_0$ parameter to control the brightness, shifting it to the right (left).


## Contributing

Pull requests are welcome. For major changes, please open an issue first
to discuss what you would like to change.

Please make sure to update tests as appropriate.

