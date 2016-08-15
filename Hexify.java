import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

/**
 * Implementacao do Projecto5
 * @author Luis Costa n47082
 *
 */
public class Hexify {
	//Atributos 
	private LabPImage image;
	private int cellSize;
	
	/**
	 * Constroi e inicializa um Hexify
	 * @param image Imagem a tratar
	 */
	public Hexify (LabPImage image) {
		this.image = image;
	}
	
	/**
	 * Devolve uma imagem do tipo LabPImage hexificada
	 * @param cellSize Lado dos hexagonos a verificar
	 * @requires cellSize > 0
	 * @return Imagem hexificada
	 */
	public LabPImage hexify (int cellSize) {
		this.cellSize = cellSize;
		List<Point> pixelsInHex;
		
		for (Point[] hex : hexagonos()) {
			//Servem para limitar ao maximo a procura de pixels
			//Tornando a sua execucao mais rapida
			double[] menores = menores(hex);
			double[] maiores = maiores(hex);
			double x = menores[0];
			double y = menores[1];
			pixelsInHex = new LinkedList<> ();
			
			while (y <= maiores[1]) {
				while (x <= maiores[0]) {
					Point pixel = new Point((int)x, (int)y);
					if (image.inImage((int)pixel.getX(), (int)pixel.getY()))
						if(isInHexagon(pixel, hex))
							pixelsInHex.add(pixel);
						x++;
				}
				x = menores[0];
				y++;
			}
			//Tratamento da cor
			setColors(pixelsInHex);
		}
		return image;
	}
	
	//Metodo privados desta classe
	/**
	 * @return Lista de pontos que representam 
	 * 		   os pontos centrais dos hexagonos
	 * 		   com tamanho cellSize usados para 
	 * 	       hexificar a imagem
	 */
	private List<Point> centers () {
		List<Point> centers = new LinkedList<>();
		double x = 0;
		double y = 0;
		double h = Math.sqrt(Math.pow(cellSize, 2) - 
				             Math.pow(cellSize / 2, 2));
		boolean isZero = true; //Onde começa a coordenada x
		int count = 0;
		
		while (image.inImage((int)x,(int)y)) {
			while (image.inImage((int)x,(int)y)) {
				centers.add(new Point((int)x,(int)y));
				x+= 3 * cellSize;
				count++; //Saber quantas vezes estender a coordenada x
			}
			//Apanhar pixels lateral direita
			centers.add(new Point((int)x,(int)y));
			//Consoante a fila o x comeca em posicoes diferentes
			if (isZero) {
				x = cellSize * 1.5;
				isZero = false;
			} else {
				x = 0;
				isZero = true;
			}
			y+= h;
		}
		//Apanhar os pixels parte inferior
		for (int i = 0; i < count; i++) {
			centers.add(new Point((int)x,(int)y));
			x+= 3 * cellSize;
		}
		return centers;
	}
	
	/**
	 * @param pc Ponto central
	 * @return Array de Pontos que representam
	 * 	       os vertices de um hexagono construido
	 * 		   a partir do ponto central pc e o proprio 
	 * 		   ponto central, vem na ultima posicao
	 */
	private Point[] hexagono (Point pc) {
		Point[] hexagono = new Point[7];
		double xc = pc.getX();
		double yc = pc.getY();
		double angle = 0;
		int last = hexagono.length - 1;
		
		for (int i = 0; i < last; i++) {
			double x = xc + cellSize * Math.cos(angle);
			double y = yc + cellSize * Math.sin(angle) * - 1;
			hexagono[i] = new Point ((int)x,(int)y);
			angle+= Math.PI / 3;
		}
		//Adicionei o ponto central por comodidade
		//em outro metodos
		hexagono[last] = pc;
		return hexagono;
	}
	
	/**
	 * @return Uma lista de Array de pontos
	 * 	       que representam os vertices dos hexagonos
	 * 	       Consoante o tamanho de centers()
	 * 
	 */
	private List<Point[]> hexagonos () {
		List<Point[]> hexagonos = new LinkedList<> ();
		
		for (Point centro : centers()) 
			hexagonos.add(hexagono(centro));
		return hexagonos;
	}
	
	/**
	 * @param pixel Ponto a confirmar
	 * @param pt Ponto a confirmar
	 * @param p1 Ponto por onde passa a recta
	 * @param p2 Ponto por onde passa a recta
	 * @return True se pixel e pc estao do mesmo lado
	 * 		   da recta ue passa em p1 e p2, 
	 * 	       caso, contrario, false
	 */
	private boolean sameSide(Point pixel, Point pc, 
			Point p1, Point p2) {
		//calcular declive da recta p1p2
		double m = (p2.getY() - p1.getY()) / 
				   (p2.getX() - p1.getX());
		//Ordenada na origem
		double b = p2.getY() - m * p2.getX();
		boolean ambosPositivos = 
				(int)(m * pixel.getX() + b - pixel.getY()) >= 0 
				&& (int)(m * pc.getX() + b - pc.getY()) >= 0;
	    boolean ambosNegativos = 
	    		(int)(m * pixel.getX() + b - pixel.getY()) <= 0 
	    		&& (int)(m * pc.getX() + b - pc.getY()) <= 0;
	    
	    return ambosPositivos || ambosNegativos;
	}
	
	/**
	 * @param pixel Ponto a considera
	 * @param tri Array de pontos que representa
	 * 		  os vertices de um triangulo
	 * @return True se pixel esta contido no triangulo
	 * 	       caso contrario, false
	 */
	private boolean isInTri (Point pixel, Point[] tri) {
		//Verifica se o ponto esta do mesmo lado
		//que o vertice to trangulo que nao passa na recta
		boolean isIn = sameSide(pixel, tri[2], tri[1], tri[0]) &&
				       sameSide(pixel, tri[0], tri[1], tri[2]) &&
				       sameSide(pixel, tri[1], tri[2], tri[0]);
		return isIn;
	}
	
	/**
	 * @param Pixel Point a verificar
	 * @param hex Array de pontos que contem
	 * 		  os vertices de um hexagono e o seu 
	 * 	      ponto central
	 * @return True se pixel esta contido dentro de hexagono
	 * 	       caso contrario, false
	 */
	private boolean isInHexagon (Point pixel, Point[] hex) {
		//Parte o hexagono em triangulos
		Point[] tri = new Point [3];
		boolean isIn = false;
		for (int i = 0; i < hex.length - 2; i++) {
			//Considera o vertice, ponto central e o proximo vertice
			tri[0] = hex[i];
			tri[1] = hex[hex.length - 1];
			tri[2] = hex[i + 1];
			
			isIn = isIn || isInTri(pixel, tri);
		}
		//ultimo triangulo
		tri[0] = hex[5];
		tri[1] = hex[hex.length - 1];
		tri[2] = hex[0];
		isIn = isIn || isInTri(pixel, tri);
		
		return isIn;
	}
	
	/**
	 * Calcula a media de cor RGB 
	 * para uma lista de pontos pixels
	 * e substitui para cada um o valor media de RGB
	 * @param list Lista de Pontos
	 */
	private void setColors (List<Point> list) {
		double mediaR = 0;
		double mediaG = 0;
		double mediaB = 0;
		int total = 0;
		
		for (Point pixel : list) {
			mediaR+= image.getPixelRed((int)pixel.getX(), 
					 (int)pixel.getY());
			mediaG+= image.getPixelGreen((int)pixel.getX(), 
					 (int)pixel.getY());
			mediaB+= image.getPixelBlue((int)pixel.getX(), 
					 (int)pixel.getY());
			total++;
		}
		mediaR = mediaR / total;
		mediaG = mediaG / total;
		mediaB = mediaB / total;
		
		for (Point pixel: list) {
			image.setPixelRGB((int)pixel.getX(), (int)pixel.getY(), 
					(int)mediaR, (int)mediaG,(int)mediaB);
		}
	}
	
	/**
	 * @param v Array de pontos
	 * @return Vetor contendo a ordenada 
	 * 	       e abcissa maior em todos 
	 *         os pontos de v
	 */
	private double[] maiores (Point[] v) {
		double[] maiores = new double[2];
		int maiorX = (int)v[0].getX();
		int maiorY = (int)v[0].getY();
		
		for (int i = 1; i < v.length; i++) {
			if ((int)v[i].getX() > maiorX)
				maiorX = (int)v[i].getX();
			if ((int)v[i].getY() > maiorY)
				maiorY = (int)v[i].getY();
		}
		maiores[0] = maiorX;
		maiores[1] = maiorY;
		return maiores;
	}
	
	/**
	 * @param v Array de pontos
	 * @return Vetor contendo a ordenada 
	 * 	       e abcissa menor em todos 
	 *         os pontos de v
	 */
	private double[] menores (Point[] v) {
		double[] menores = new double[2];
		int menorX = (int)v[0].getX();
		int menorY = (int)v[0].getY();
		
		for (int i = 1; i < v.length; i++) {
			if ((int)v[i].getX() < menorX)
				menorX = (int)v[i].getX();
			if ((int)v[i].getY() < menorY)
				menorY = (int)v[i].getY();
		}
		menores[0] = menorX;
		menores[1] = menorY;
		return menores;
	}
}
