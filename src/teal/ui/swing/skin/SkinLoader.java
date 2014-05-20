
package teal.ui.swing.skin;

import java.awt.*;
import java.awt.image.*;
import java.net.URL;

public class SkinLoader implements ImageObserver {

    Image image = null;

    public SkinLoader(String imageFile, Toolkit tk) throws Exception {
        URL url = this.getClass().getClassLoader().getResource("teal/swing/plaf/teallnf/icons/" + imageFile);
        image = tk.createImage(url);
        synchronized (this) {
            if (!tk.prepareImage(image, -1, -1, this)) {
                this.wait();
            }
        }
        //System.out.println("The image ("+imageFile+") is now completely loaded");
    }

    public Image getImage() {
        return image;
    }

    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
        synchronized (this) {
            if ((infoflags & ImageObserver.ALLBITS) == ImageObserver.ALLBITS) {
                this.notify();
                return false;
            } else {
                return true;
            }
        }
    }
}