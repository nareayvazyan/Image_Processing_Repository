File.openSequence("C:/Users/user/Desktop/Nare Ayvazyan/AUA/Summer 2024/Image Processing Suren Khachatryan/HW1_Image_Processing/Nare_nonzip_original/", " filter=-11");
run("Gaussian Blur...", "sigma=3 stack");
run("Median...", "radius=3 stack");
run("Compile and Run...", "compile=C:/Users/user/Downloads/ij154-win-java8/ImageJ/plugins/HSV_Threshold.java");
run("Make Binary", "calculate");
run("Invert", "stack");
makeOval(228, 112, 199, 232);
run("Make Inverse");
setBackgroundColor(0, 0, 0);
run("Clear", "stack");
run("Image Sequence... ", "dir=C:/Users/user/Desktop/HW3_IP/ format=PNG use");

