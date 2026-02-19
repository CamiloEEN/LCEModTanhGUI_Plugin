# ModTanhCeenGUI_Plugin

This is an ImageJ plugin that implements an analytical, non-linear transformation for local contrast enhancement in digital images.

It is based on the research article: ["Local Contrast Enhancement in Digital Images Using a Tunable Modified Hyperbolic Tangent Transformation"] (https://doi.org/10.3390/math14030571). The plugin offers a high-performance alternative to traditional techniques like Histogram Equalization (HE), Histogram Specification (HS), and CLAHE, with a specific focus on optimizing contrast in Medical Resonance (MR) images.

The tool is designed as a Java-based plugin for the [ImageJ software ecosystem](https://imagej.net/ij/).


## Installation

1. First download the most recent version of ImageJ from the official [page](https://imagej.net/ij/download.html).
2. Copy the file ModTanhCEEN_RGB_GUI.java into the folder ImageJInstallationPath/ImageJ/plugins.
3. Run ImageJ and in the main tool bar select plugins-> Compile and Run
4. Select the file ModTanhCEEN_RGB_GUI.java and you are good to go.

Note 1: If there is no image open previously the plugin will not run and only give a warning. However after it is compiled it can be accesed from the plugins menu.

Note 2: At the moment the plugin only works with RGB images like .png and .jpg, for 8-bit images you can just convert those to RGB (from the menu bar Image > Type > RGB Color) then perform the same transformation in all channels to achieve the desired effect.


## Usage

ModTanhCeenGUI_Plugin opens two windows:

 - MainWindow with the image and a side panel which allows to localy increase the contrast around a particular pixel intensity region.
 - A plot window which shows the current pixel to pixel transformation applied to it.

For most purposes only the $q_0$ and $\lambda$ parameters are relevant to enhance contrast. The whole process can be simplified as follows:

1. Set $q_0$ equal to a pixel intensity value in the region of interest.
2. Start increasing the $\lambda$ parameter from 0 until the resulting image exhibits the best contrast.
3. (Optional) If the resulting image appears too bright (dark), adjust the $q_0$ parameter to control the brightness, shifting it to the right (left).


