
import java.io.IOException;
import java.util.Scanner;

class RunHexify {
    
    static void transform() throws IOException {
        Scanner teclado = new Scanner(System.in);
        System.out.println("Imagem a transformar: ");
        String imageFile = teclado.next();
        System.out.println("Tamanho do hexagono (lado): ");
        int cellSize = teclado.nextInt();
        LabPImage im = LabPImage.makeLabPImage(imageFile);
        Hexify im2 = new Hexify(im);
        LabPImage im3 = im2.hexify(cellSize);
        im3.writeImageToPNG(
            imageFile.substring(0, imageFile.lastIndexOf('.')) + 
            "-hex" + cellSize + ".png");
        teclado.close();
    }
    
    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {        
        transform();
    }
}