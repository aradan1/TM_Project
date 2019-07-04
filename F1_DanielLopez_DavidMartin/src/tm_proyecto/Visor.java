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
        private JLabel lb1ImageHolder;
        private JLabel lb2ImageHolder;

        public Visor() {
                this.contentPane = new JPanel();
                this.setContentPane(contentPane);
                lb1ImageHolder = new JLabel("");
                contentPane.add(lb1ImageHolder, BorderLayout.WEST);
                lb2ImageHolder = new JLabel("");
                contentPane.add(lb2ImageHolder, BorderLayout.EAST);

        }

        /**
         * Actualiza la imagen, se llama en cada frame
         */
        public void changeFrame(){

                if (imagen != null) {
                        ImageIcon icono1 = new ImageIcon((Image) imagen);
                        lb1ImageHolder.setIcon(icono1);
                        this.setSize(icono1.getIconWidth(), icono1.getIconHeight());
                        ImageIcon icono2 = new ImageIcon((Image) imagen);
                        lb2ImageHolder.setIcon(icono2);
                        this.setSize(icono2.getIconWidth(), icono2.getIconHeight());
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