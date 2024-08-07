import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ij.text.TextWindow;

import java.util.Set;
import java.util.TreeSet;

public class Task_3_HW_2 implements PlugInFilter {
    // Set to store unique colors
    private Set<Integer> distinct_colors = new TreeSet<>();
    
    // Arrays to store the count, min, max, total mean, and total mean squared values
    private int[] green_count = new int[256];
    private int[] green_min = new int[256];
    private int[] green_max = new int[256];
    private long[] sum_rb_mean = new long[256];
    private long[] sum_rb_mean_sq = new long[256];

    // Variables to track the total number of slices and the current slice
    private int num_slices;
    private int slice_index;

    // Setup method to initialize the filter and set min and max arrays
    @Override
    public int setup(String args, ImagePlus imp) {
        // Initialize min values to maximum integer and max values to minimum integer
        for (int i = 0; i < 256; i++) {
            green_min[i] = Integer.MAX_VALUE;
            green_max[i] = Integer.MIN_VALUE;
        }
        num_slices = imp.getStackSize(); // Get the total number of slices in the stack
        slice_index = 0; // Initialize the current slice to 0
        return DOES_RGB | DOES_STACKS; // Indicate that the plugin works on RGB and stacks
    }

    // Method to add color to the set of distinct colors
    private void register_color(int color) {
        if ((color & 0xFFFFFF) != 0) {
            distinct_colors.add(color);
        }
    }

    // Run method to process each image slice
    @Override
    public void run(ImageProcessor ip) {
        int width = ip.getWidth();
        int height = ip.getHeight();

        // Iterate through each pixel in the image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int[] pixel = ip.getPixel(x, y, null);
                int r = pixel[0];
                int g = pixel[1];
                int b = pixel[2];
                int color = (r << 16) | (g << 8) | b; // Combine RGB values into a single integer
                register_color(color); // Add color to set if not black
            }
        }

        process_colors(); // Process each distinct color

        distinct_colors.clear(); // Clear the set for the next slice
        slice_index++;
        if (slice_index == num_slices) {
            display_results(); // Show results if it's the last slice
        }
    }

    // Process each distinct color
    private void process_colors() {
        for (int color : distinct_colors) {
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;

            int rb_mean = (r + b) / 2; // Calculate mean of red and blue components
            int rb_mean_sq = rb_mean * rb_mean; // Calculate squared mean of red and blue components

            green_count[g]++;
            green_min[g] = Math.min(green_min[g], rb_mean); // Update min value for the green component
            green_max[g] = Math.max(green_max[g], rb_mean); // Update max value for the green component
            sum_rb_mean[g] += rb_mean; // Accumulate total mean for the green component
            sum_rb_mean_sq[g] += rb_mean_sq; // Accumulate total squared mean for the green component
        }
    }

    // Method to display the results in a text window
    private void display_results() {
        int[] mean_rb = new int[256];
        int[] mean_rb_sq = new int[256];

        // Calculate mean and mean squared values for each green component
        for (int i = 0; i < 256; i++) {
            if (green_count[i] > 0) {
                mean_rb[i] = (int) (sum_rb_mean[i] / green_count[i]);
                mean_rb_sq[i] = (int) (sum_rb_mean_sq[i] / green_count[i]);
            } else {
                green_min[i] = 0;
                green_max[i] = 0;
                mean_rb[i] = 0;
                mean_rb_sq[i] = 0;
            }
        }

        // Build the output string
        StringBuilder output = new StringBuilder();
        output.append("G\tCount\tMin\tMax\tMean\tMean2\n");
        for (int i = 0; i < 256; i++) {
            output.append(String.format("%d\t%d\t%d\t%d\t%d\t%d\n",
                    i, green_count[i], (green_min[i] == Integer.MAX_VALUE ? 0 : green_min[i]),
                    (green_max[i] == Integer.MIN_VALUE ? 0 : green_max[i]), mean_rb[i], mean_rb_sq[i]));
        }

        // Display the results in a text window named "Task_3_HW_2"
        new TextWindow("Task_3_HW_2", output.toString(), 700, 500);
    }
}
