package tm_proyecto;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;

/**
 * 
 * Crea un panel en el que mostrar las imagenes como vídeo
 */
public class Visor extends JFrame {

        private BufferedImage imagen;
        private JPanel contentPane;
        private JLabel lblImageHolder;

        public Visor() {
                this.contentPane = new JPanel();
                this.setContentPane(contentPane);
                lblImageHolder = new JLabel("Image Holder");
                contentPane.add(lblImageHolder, BorderLayout.CENTER);

        }

        /**
         * Actualiza la imagen, se llama en cada frame
         */
        public void changeFrame(){

                if (imagen != null) {
                        ImageIcon icono = new ImageIcon((Image) imagen);
                        lblImageHolder.setIcon(icono);
                        this.setSize(icono.getIconWidth(), icono.getIconHeight());
                }
                this.show();
        }
        
        /**
         * Cambia la imagen
         * @param imagen 
         */
        public void setImage(BufferedImage imagen){
            this.imagen = imagen;
        }

}