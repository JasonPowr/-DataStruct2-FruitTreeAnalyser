package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;


public class Controller {
    public ImageView originalImage, analysedImage, blackAndWhiteImage;
    public Button analyseImage;
    public ComboBox fruitSelection;
    public TextField totalFruit;
    public CheckBox colorAllFruits;
    public CheckBox colorSelectedFruits;
    public Tab blackAndWhitePane;

    public int counter = 0;
    public Rectangle rect = new Rectangle();
    public ArrayList<Integer> estSize = new ArrayList<>();
    public ArrayList<Rectangle> rectangles = new ArrayList<>();
    public ToggleButton onScreenLabel;
    public Button clearButton;

    public void initialize() {
        setFruitSelection();
    }

    public WritableImage selectImage() {
        FileChooser fc = new FileChooser();
        File img = fc.showOpenDialog(null);

        String imageUrl = "file:///" + img.getPath();          //Method for selecting an image using FileChooser and displaying the orginal image
        Image image = new Image(imageUrl, 902, 545, false, false);
        originalImage.setImage(image);
        WritableImage wimg = new WritableImage(image.getPixelReader(), (int) image.getWidth(), (int) image.getHeight());
        return wimg;

    }

    public void setFruitSelection() {
        fruitSelection.getItems().add("Orange");
        fruitSelection.getItems().add("Apple");             //populates the values for the combo box
        fruitSelection.getItems().add("Blueberry");
    }


    public String getFruitSelection() {
        return fruitSelection.getValue().toString();            //getter for fruit seletion
    }


    public void analyseImage() {
        String fruitChoice = fruitSelection.getValue().toString();
        switch (fruitChoice) {
            case "Orange":
                blackAndWhiteConversionForOrange();
                break;
            //switch case for getting fruit decision
            case "Apple":
                blackAndWhiteConversionForApple();
                break;

            case "Blueberry":
                blackAndWhiteConversionForBlueBerry();
                break;
        }
    }

    public void blackAndWhiteConversionForOrange() {
        WritableImage img = selectImage();
        PixelReader pr = img.getPixelReader();
        PixelWriter pw = img.getPixelWriter();

        for (int r = 0; r < img.getHeight(); r++) {
            for (int c = 0; c < img.getWidth(); c++) {

                Color color = pr.getColor(c, r);
                Color white = Color.rgb(255, 255, 255);
                Color black = Color.rgb(0, 0, 0);

                if ((color.getHue()) > 0 && (color.getHue() < 60)) {
                    pw.setColor(c, r, white);
                } else
                    pw.setColor(c, r, black);
            }
        }
        blackAndWhiteImage.setImage(img);
        analysis(img);
    }

    public void blackAndWhiteConversionForApple() {
        WritableImage img = selectImage();
        PixelReader pr = img.getPixelReader();
        PixelWriter pw = img.getPixelWriter();

        for (int r = 0; r < img.getHeight(); r++) {
            for (int c = 0; c < img.getWidth(); c++) {

                Color color = pr.getColor(c, r);
                Color white = Color.rgb(255, 255, 255);
                Color black = Color.rgb(0, 0, 0);

                if ((color.getHue()) > 320 && (color.getHue() < 360)) {
                    pw.setColor(c, r, white);
                } else
                    pw.setColor(c, r, black);
            }
        }
        blackAndWhiteImage.setImage(img);
        analysis(img);
    }

    public void blackAndWhiteConversionForBlueBerry() {
        WritableImage img = selectImage();
        PixelReader pr = img.getPixelReader();
        PixelWriter pw = img.getPixelWriter();

        for (int r = 0; r < img.getHeight(); r++) {
            for (int c = 0; c < img.getWidth(); c++) {

                Color color = pr.getColor(c, r);
                Color white = Color.rgb(255, 255, 255);
                Color black = Color.rgb(0, 0, 0);

                if ((color.getHue()) > 180 && (color.getHue() < 300)) {
                    pw.setColor(c, r, white);
                } else
                    pw.setColor(c, r, black);
            }
        }
        blackAndWhiteImage.setImage(img);
        analysis(img);
    }


    public static int find(int[] a, int id) {
        if (a[id] < 0)
            return a[id];

        if (a[id] == id)
            return id;

        else return find(a, a[id]);
    }

    public static void union(int[] a, int p, int q) {
        a[find(a, q)] = find(a, p);
    }


    public void analysis(WritableImage imageforAnalysis) {
        PixelReader pr = imageforAnalysis.getPixelReader();

        HashMap<Integer, ArrayList<Integer>> fruits = new HashMap<>();
        int[] arrayForImage = new int[(int) (imageforAnalysis.getWidth() * imageforAnalysis.getHeight())];

        int i = 0;
        for (int r = 0; r < imageforAnalysis.getHeight(); r++) {
            for (int c = 0; c < imageforAnalysis.getWidth(); c++) {
                Color color = pr.getColor(c, r);

                i = (int) (r * imageforAnalysis.getWidth() + c);

                if (color.equals(Color.BLACK)) {
                    arrayForImage[i] = -1;
                } else {
                    arrayForImage[i] = i;
                }
            }
        }

        int width = (int) imageforAnalysis.getWidth();
        int key = 0;

        for (int e = 0; e < arrayForImage.length; e++) {
            if (arrayForImage[e] >= 0) {
                if ((e + 1) < arrayForImage.length && (arrayForImage[e + 1] >= 0) && ((e + 1) % width > 0)) {
                    union(arrayForImage, e, e + 1);
                }
                if ((e + width) < arrayForImage.length && (arrayForImage[e + width] >= 0)) {
                    union(arrayForImage, e, e + width);
                }
            }

        }

        for (int b = 0; b < arrayForImage.length; b++) {
            if (arrayForImage[b] >= 0) {
                key = find(arrayForImage, b);

                if (fruits.get(key) == null) {
                    fruits.put(key, new ArrayList<Integer>());
                }
                fruits.get(key).add(b);

            }
        }
        drawRectangle(fruits, imageforAnalysis);
    }

    public void drawRectangle(HashMap<Integer, ArrayList<Integer>> fruits, WritableImage imageForAnalysis) {
        int imageWidth = (int) imageForAnalysis.getWidth();
        int counter = 0;

        for (int i : fruits.keySet()) {

            ArrayList<Integer> rectArray = fruits.get(i);

            if (rectArray.size() > 300) {
                int startX, bottomX = rectArray.get(0) % imageWidth; //colom
                int startY, bottomY = rectArray.get(0) / imageWidth; //row


                startX = bottomX;
                startY = bottomY;

                for (int a = 0; a < rectArray.size(); a++) {
                    if (rectArray.get(a) % imageWidth < startX) {
                        startX = rectArray.get(a) % imageWidth;
                    }
                    if (rectArray.get(a) % imageWidth > bottomX) {
                        bottomX = rectArray.get(a) % imageWidth;
                    }
                    if (rectArray.get(a) / imageWidth < startY) {
                        startY = rectArray.get(a) / imageWidth;
                    }
                    if (rectArray.get(a) / imageWidth > bottomY) {
                        bottomY = rectArray.get(a) / imageWidth;
                    }
                }


                rect = new Rectangle(startX, startY, bottomX - startX, bottomY - startY);
                rectangles.add(rect);

                rect.setFill(Color.TRANSPARENT);
                rect.setStroke(Color.BLUE);
                rect.setStrokeWidth(3);
                rect.setTranslateX(analysedImage.getLayoutX());
                rect.setTranslateY(analysedImage.getLayoutY());

                Image ogImage = originalImage.getImage();
                analysedImage.setImage(ogImage);

                ((Pane) analysedImage.getParent()).getChildren().add(rect);
                counter++;


                int estimatedSize = (int) (rect.getHeight() * rect.getWidth());

                estSize.add(estimatedSize);
                estSize.sort(Comparator.reverseOrder());

                Tooltip mousePositionToolTip = new Tooltip("");
                rect.setOnMouseEntered(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        int indexOf = ((estSize.indexOf(estimatedSize))) + 1;
                        String msg = "Fruit/Cluster Number : " + indexOf + "\n" +
                                "Estimated Size : " + estimatedSize;
                        mousePositionToolTip.setText(msg);
                        mousePositionToolTip.show(rect, event.getScreenX() + 20, event.getScreenY());
                    }

                });

                rect.setOnMouseExited(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        mousePositionToolTip.hide();
                    }
                });
            }
            this.counter = counter;
        }
        totalFruit.setText(String.valueOf(counter));
    }


    public void colourAllFruits() {

    }

    public void onScreenLabel(ActionEvent event) {
        boolean isSelected = onScreenLabel.isSelected();
        StackPane stackPane = new StackPane();
        Text text = new Text();

        if (isSelected) {
            stackPane.setLayoutX(analysedImage.getLayoutX());
            stackPane.setLayoutY(analysedImage.getLayoutY());
            System.out.println(estSize);
            System.out.println(rectangles);

            int indexOf = 0;
            for (int i = 0; i < estSize.size(); i++) {
                indexOf = estSize.indexOf((int) rectangles.get(i).getWidth() * (int) rectangles.get(i).getHeight());
                text = new Text("" + (indexOf + 1));
                text.setTranslateX(rectangles.get(i).getX());
                text.setTranslateY(rectangles.get(i).getY());
                text.setFill(Color.WHITE);
                stackPane.getChildren().add(text);
            }
            ((Pane) analysedImage.getParent()).getChildren().add(stackPane);
        }
    }

    public void colourSelectFruits() {

    }
}






























