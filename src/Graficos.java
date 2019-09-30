import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class Graficos extends Canvas implements Runnable{
	
	public static JFrame frame;
	private Thread thread;
	private boolean isRunning = true;
	private final int WIDTH = 240; //Largura da janela grafica
	private final int HEIGHT = 160; // Altura da janela grafica
	private final int SCALE = 3; // Escala da janela grafica
	
	private BufferedImage image;
	
	private Spritesheet sheet;
	private BufferedImage[] player;
	private int frames = 0;
	private int maxFrames = 20; //Quanto menor mais rapido
	private int curAnimation = 0, maxAnimation = 3;
	
	public Graficos(){
		sheet = new Spritesheet("/Spritesheet.png");
		player = new BufferedImage[4];
		player[0] = sheet.getSprite(0, 0, 16, 16);
		player[1] = sheet.getSprite(16, 0, 16, 16);
		player[2] = sheet.getSprite(32, 0, 16, 16);
		player[3] = sheet.getSprite(48, 0, 16, 16);
		
		setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		initFrame();
		image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB); //largura, altura, tipo da imagem
	}
	
	public void initFrame() {
		frame = new JFrame("Teste"); //Inicializa o objeto e pode deixar o nome na janela
		frame.add(this); //Tudo pertence ao Canvas, isso permite pegar todas as informaçoes de "frame"
		frame.setResizable(false); // Para o usuario não consegui redimencionar a janela
		frame.pack(); //Metodo do frames para quando colocar o canvas calcular certas dimensoes e mostrar
		frame.setLocationRelativeTo(null); //Para a janela ficar no centro
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Para quando fechar a janela, fechar tambem o programa
		frame.setVisible(true); //Para quando inicializar a janela ja estar visivel
	}
	
	public synchronized void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}
	
	public synchronized void stop() {
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Graficos game = new Graficos();
		game.start(); // Aqui o jogo inicia de fato
	}
	
	/*Cuida da logica do jogo*/
	public void tick() {
		frames++;
		if(frames > maxFrames) {
			frames = 0;
			curAnimation++;
			if(curAnimation >= maxAnimation) {
				curAnimation = 0;
			}
		}
	}
	
	/*Cuida da renderizaçao*/
	public void render() {
		BufferStrategy bs = this.getBufferStrategy(); // Uma sequencia de buffers que coloca na tela pra otimizar a renderizacao
		if(bs == null) { //Vai servir para criar o buffer na primeira vez que renderizar, nas proximas vezes não entra mais nessa condicao 
			this.createBufferStrategy(3);
			return; //Esta funcionando como um break
			
		}
		
		Graphics g = image.getGraphics(); //Para começar a renderizar na tela
		g.setColor(Color.BLUE); //Para definir a cor padrao da tela 
		g.fillRect(0, 0, WIDTH, HEIGHT); //Renderizando um retangulo
		
		/*g.setColor(Color.blue); 
		g.fillOval(0, 0, 100, 100);*/
		
		/*g.setFont(new Font("Arial", Font.BOLD, 16));
		g.setColor(Color.WHITE);
		g.drawString("Olá mundo", 60, 60);*/
		
		Graphics2D g2 = (Graphics2D) g; //Transforma o objeto no Graphics2D e permite que crie animaçoes e efeitos 
		g2.setColor(new Color(0, 0, 0, 200));
		g2.fillRect(0, 0, WIDTH, HEIGHT);
		g2.rotate(Math.toRadians(0), 90+8, 90+8);
		g2.drawImage(player[curAnimation], 90, 90, null);
		
		g.dispose(); //Limpar dados que tem na imagem que nao precisa que ja foram usado antes (melhora a performance)
		
		g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null);
		bs.show(); //Para mostrar de fato os graficos
	}
	
	
	@Override
	public void run() {
		long lastTime = System.nanoTime(); //Pega a hora atual do nosso computador e transforma em nanossegundos para ter precisao
		double amountOfTicks = 60.0; // frames per second
		double ns = 1000000000 / amountOfTicks; // dividindo 1 segundo
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();//Pega a hora atual tambem, apenas mais leve e menos preciso
		while(isRunning) {
			long now = System.nanoTime();
			delta+= (now - lastTime) / ns; //Para que a cada segundo dê um update
			lastTime = now;
			if(delta >= 1) { // para que atualize o jogo a cada segundo
				tick(); //primeiro atualiza
				render(); //depois renderiza
				frames++;
				delta--;
			}
			
			if(System.currentTimeMillis() - timer >= 1000){ // Passou um segundo desde que mostrou a ultima mensagem
				System.out.println("FPS: "+ frames);
				frames = 0;
				timer += 1000; // Para mostrar a cada segundo
			}
		}
		
		stop();
	}

}
