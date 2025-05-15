package phong;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Phong {
    public static class Scene {
        public static class LightSource {
            public double[] location;
            public double[] intensity;
        }

        public static class Material {
            public double[] diffuseReflection;
            public double[] specularReflection;
            public double glossiness;
            public double[] ambientLightReflection;
            public double[] selfLuminance;
        }

        public List<LightSource> lights;
        public double[] ambientIntensity;
        public double[] attenuation;
        public Material material;
        public int width;
        public int height;
        public String name;
    }

    static class Sphere {
        double radius;
        double[] center;

        public Sphere(double radius, double[] center) {
            this.radius = radius;
            this.center = center;
        }

        public double[] intersect(double x, double y) {
            double r2 = radius * radius;
            double xy2 = x * x + y * y;

            if (xy2 >= r2) {
                return null;
            }

            double z = Math.sqrt(r2 - xy2);
            return new double[]{x + center[0], y + center[1], z + center[2]};
        }

        public double[] normalAt(double[] point) {
            double[] normal = {
                    point[0] - center[0],
                    point[1] - center[1],
                    point[2] - center[2]
            };
            return normalize(normal);
        }
    }

    private static double[] normalize(double[] v) {
        double length = vectorLength(v);
        if (length == 0) return v;

        return new double[]{
                v[0] / length,
                v[1] / length,
                v[2] / length
        };
    }

    private static double dotProduct(double[] a, double[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }

    private static double[] subtractVectors(double[] a, double[] b) {
        return new double[]{
                a[0] - b[0],
                a[1] - b[1],
                a[2] - b[2]
        };
    }

    private static double[] reflectVector(double[] vector, double[] normal) {
        double dot = dotProduct(vector, normal);
        return new double[]{
                vector[0] - 2 * dot * normal[0],
                vector[1] - 2 * dot * normal[1],
                vector[2] - 2 * dot * normal[2]
        };
    }

    // Długość wektora
    private static double vectorLength(double[] v) {
        return Math.sqrt(dotProduct(v, v));
    }

    private static double calculateAttenuation(double[] coefs, double distance) {
        double c2 = coefs[2]; // Kwadratowy
        double c1 = coefs[1]; // Liniowy
        double c0 = coefs[0]; // Stały

        return Math.min(1.0, 1.0 / (c2 * distance * distance + c1 * distance + c0));
    }

    private static double[] calculatePhongLighting(Scene scene, double[] hitPoint, double[] normal, double[] observer) {
        double[] result = new double[3];
        Scene.Material material = scene.material;

        for (int i = 0; i < 3; i++) {
            result[i] = material.selfLuminance[i]; // S_c
            result[i] += scene.ambientIntensity[i] * material.ambientLightReflection[i]; // A_c * k_aC
        }

        for (Scene.LightSource light : scene.lights) {
            double[] lightVector = subtractVectors(light.location, hitPoint);
            double distanceToLight = vectorLength(lightVector); // r_i

            double[] lightDirection = normalize(lightVector); // I_i

            double attenuation = calculateAttenuation(scene.attenuation, distanceToLight); // f_att(r_i)

            double[] viewDirection = normalize(subtractVectors(observer, hitPoint)); // O
            double[] observerReflection = reflectVector(viewDirection, normal); // O_S

            double diffuseFactor = Math.max(0, dotProduct(normal, lightDirection)); // N . I_i

            double specularFactor = Math.pow(
                    Math.max(0, dotProduct(lightDirection, observerReflection)), // (I_i . O_S)^g
                    material.glossiness
            );

            for (int i = 0; i < 3; i++) {
                double lightIntensity = light.intensity[i] * attenuation; //f_att(r_i) * E_iC

                // k_dC * (N . I_i) * (f_att(r_i) * E_iC)
                result[i] += material.diffuseReflection[i] * diffuseFactor * lightIntensity;
                //k_sC * (I_i . O_S)^g * (f_att(r_i) * E_iC)
                result[i] += material.specularReflection[i] * specularFactor * lightIntensity;

                result[i] = Math.min(1.0, Math.max(0.0, result[i]));
            }
        }

        return result;
    }

    private static Color vectorToColor(double[] colorVector) {
        int r = (int) (colorVector[0] * 255);
        int g = (int) (colorVector[1] * 255);
        int b = (int) (colorVector[2] * 255);

        return new Color(
                Math.min(255, Math.max(0, r)),
                Math.min(255, Math.max(0, g)),
                Math.min(255, Math.max(0, b))
        );
    }

    public static BufferedImage renderScene(Scene scene, int width, int height) {

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Sphere sphere = new Sphere(0.5, new double[]{0, 0, 0});

        double[] observerCords = {0, 0, -100};

        double aspectRatio = (double) width / height;
        double viewWindowSize = 1.0;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Color pixelColor = Color.BLACK;

                double planeX = viewWindowSize * aspectRatio * (2.0 * j / (width - 1) - 1);
                double planeY = viewWindowSize * (1.0 - 2.0 * i / (height - 1));

                double[] hitPoint = sphere.intersect(planeX, planeY);

                if (hitPoint != null) {
                    double[] normal = sphere.normalAt(hitPoint);
                    double[] colorVector = calculatePhongLighting(scene, hitPoint, normal, observerCords);

                    pixelColor = vectorToColor(colorVector);
                }

                image.setRGB(j, i, pixelColor.getRGB());
            }
        }

        return image;
    }

    public static void saveImage(BufferedImage image, String fileName) throws IOException {
        fileName = "src/phong/renders/" + fileName + ".png";
        File outputFile = new File(fileName);
        ImageIO.write(image, "png", outputFile);
    }

    public static void showImage(String fileName, int width, int height) {
        JFrame frame = new JFrame("Phong Renderer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);

        ImageIcon imageIcon = new ImageIcon(fileName);
        JLabel imageLabel = new JLabel(imageIcon);

        frame.add(imageLabel);
        frame.setVisible(true);
    }

    public static void main(String[] args) throws IOException {
        String[] presetNames = {
                "src/phong/presets/Default Gray.json",
                "src/phong/presets/Matte red plastic.json",
                "src/phong/presets/White gypsum plaster.json",
                "src/phong/presets/Pink gypsum plaster.json",
                "src/phong/presets/Cyan gypsum plaster.json",
                "src/phong/presets/Glossy white plastic.json",
                "src/phong/presets/Matte green plastic.json",
                "src/phong/presets/Glossy white plastic 3 lights.json"
        };

        int width = 800;
        int height = 800;

        ObjectMapper mapper = new ObjectMapper();

        for (String presetName : presetNames) {
            System.out.println("Rendering preset: " + presetName);

            try {
                Scene scene = mapper.readValue(new File(presetName), Scene.class);
                BufferedImage renderedImage = renderScene(scene, scene.width, scene.height);

                saveImage(renderedImage, scene.name);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        showImage("src/phong/renders/Default Gray.png", width, height);
    }

}
