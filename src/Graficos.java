import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

public class Graficos extends Canvas implements Runnable{
	
	public static JFrame frame;
	private Thread thread;
	private boolean isRunning = true;
	private final int WIDTH = 160; //Largura da janela grafica
	private final int HEIGHT = 120; // Altura da janela grafica
	private final int SCALE = 3; // Escala da janela grafica
	
	public Graficos(){
		setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		initFrame();
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
		
	}
	
	public static void main(String[] args) {
		Graficos game = new Graficos();
		game.start(); // Aqui o jogo inicia de fato
	}
	
	//Cuida da logica do jogo
	public void tick() {
		
	}
	
	//Cuida da renderizaçao
	public void render() {
		BufferStrategy bs = this.getBufferStrategy(); // Uma sequencia de buffers que coloca na tela pra otimizar a renderizacao
		if(bs == null) { //Vai servir para criar o buffer na primeira vez que renderizar, nas proximas vezes não entra mais nessa condicao 
			this.createBufferStrategy(3);
			return; //Esta funcionando como um break
			
		}
		
		Graphics g = bs.getDrawGraphics(); //Para começar a renderizar na tela
		g.setColor(new Color(19,19,19));
		g.fillRect(0, 0, 160, 120); //Renderizando um retangulo
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
	}

}
