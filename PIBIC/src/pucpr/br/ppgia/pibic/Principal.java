package pucpr.br.ppgia.pibic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections15.Transformer;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import pucpr.br.pggia.pibic.dao.pibicDAO;
import pucpr.br.ppgia.pibic.model.Commit;
import pucpr.br.ppgia.pibic.model.Project;
import pucpr.br.ppgia.pibic.model.User;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.GraphMouseListener;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class Principal {

	private VisualizationViewer<String, MyLink> vv;
	private VisualizationViewer<String, Integer> vs;

	public static void main(String[] args) {
		Principal p = new Principal();

		JFileChooser chooser = new JFileChooser();
		// Label Attachment 
		int option = chooser.showOpenDialog(new JFrame()); // parentComponent
															// must a component
															// like JFrame,
															// JDialog...
		if (option == JFileChooser.APPROVE_OPTION) {
			File selectedFile = chooser.getSelectedFile();
			String path = selectedFile.getAbsolutePath();
			List<Commit> commits = p.lerDadosCommit(path);
			
			//fechar issue
			// isto é um comentário de teste
			//p.salvarDadosCommit(path); // Não é necessário salvar os commits toda vez.

			Project project = p.lerProjeto(path);

			// List<Project> projects = p.lerDadosProjetosUser("");

			// p.salvarDadosProjeto(projects);

			// p.criarGrafoUser(projects);
			
			p.criarGrafo(commits, project);
		}


	}

	public Project lerProjeto(String path) {
//		File fXmlFile = new File("C:/Users/projeto/workspace/PIBIC/data/data-airbnb-airpal.xml");
		File fXmlFile = new File(path);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;

// testando outro commit de contributor
		Project p;

		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();

			String project = doc.getDocumentElement().getAttribute("name");

			p = new Project();

			p.setName(project);
			
			return p;
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	public int edgeCount;

	public void salvarDadosCommit(String path) {
		Principal p = new Principal();

		// File fXmlFile = new File("./data/data-free-for-dev.xml");
		//File fXmlFile = new File("./data/data-airbnb-airpal.xml");
		File fXmlFile = new File(path);

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		List<Commit> commits = new ArrayList<Commit>();

		Commit commit;

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date data;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();

			String s;

			NodeList elements = doc.getElementsByTagName("element");
			for (int cont = 0; cont < elements.getLength(); cont++) {

				commit = new Commit();
				Node elementNode = elements.item(cont);

				Element element = (Element) elementNode;

				NodeList shas = element.getElementsByTagName("sha");

				Node sha = shas.item(shas.getLength() - 1);

				String sSha;
				sSha = sha.getTextContent();

				commit.setSha(sSha);

				NodeList nList = element.getElementsByTagName("commit");

				for (int temp = 0; temp < nList.getLength(); temp++) {

					Node nNode = nList.item(temp);

					// System.out.println("\nCurrent Element :" +
					// nNode.getNodeName());

					Element eElement = (Element) nNode;

					NodeList nlCommit = eElement.getElementsByTagName("author");

					for (int i = 0; i < nlCommit.getLength(); i++) {

						Element n = (Element) nlCommit.item(i);

						s = n.getElementsByTagName("date").item(0)
								.getTextContent();
						s = s.substring(0, 10);

						data = df.parse(s);

						commit.setDate(data);
						commit.setNome(n.getElementsByTagName("name").item(0)
								.getTextContent());

					}

					commit.setMessage(eElement.getElementsByTagName("message")
							.item(0).getTextContent());

					Project project = p.lerProjeto(path);

					commit.setProject(project);

					commits.add(commit);
				}
			}

			new pibicDAO().inserirCommit(commits);

			// p.criarGrafo(commits, project);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void salvarDadosProjeto(List<Project> projects) {
		new pibicDAO().inserirProjetos(projects);
	}

	public List<Project> lerDadosProjetosUser(String path) {

		File fXmlFile = new File("./data/data-ripienaar-repositories.xml");
		// File fXmlFile = new File(path);

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		Project pr;
		List<Project> projects = new ArrayList<Project>();
		User u;

		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();

			NodeList elements = doc.getElementsByTagName("element");
			for (int cont = 0; cont < elements.getLength(); cont++) {
				pr = new Project();
				Node elementNode = elements.item(cont);

				Element element = (Element) elementNode;

				NodeList names = element.getElementsByTagName("name");

				NodeList languages = element.getElementsByTagName("language");

				NodeList owner = element.getElementsByTagName("owner");

				Node ownerNode = owner.item(0);

				Element ownerElement = (Element) ownerNode;

				NodeList login = ownerElement.getElementsByTagName("login");

				System.out.println(login.item(0).getTextContent());

				u = new User();
				u.setUser(login.item(0).getTextContent());
				pr.setUser(u);
				pr.setName(names.item(0).getTextContent());
				pr.setLanguage(languages.item(0).getTextContent());

				projects.add(pr);
			}

			return projects;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Commit> lerDadosCommit(String path) {
		// p.criarGrafo();

		// File fXmlFile = new File("./data/data.xml");
		// File fXmlFile = new File("./data/data-poliglot.xml");
		// File fXmlFile = new File("./data/data-free-for-dev.xml");
		//File fXmlFile = new File("./data/data-airbnb-airpal.xml");
		// File fXmlFile = new File("./data/data-java-design-patterns.xml");
		File fXmlFile = new File(path);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		List<Commit> commits = new ArrayList<Commit>();

		Commit commit;
		boolean contemAutor = false;

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date data;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();

			System.out.println("Root element :"
					+ doc.getDocumentElement().getNodeName());

			String s;

			Project pr = this.lerProjeto(path);

			NodeList elements = doc.getElementsByTagName("element");
			for (int cont = 0; cont < elements.getLength(); cont++) {

				commit = new Commit();
				Node elementNode = elements.item(cont);

				Element element = (Element) elementNode;

				NodeList shas = element.getElementsByTagName("sha");

				Node sha = shas.item(shas.getLength() - 1);

				String sSha;
				sSha = sha.getTextContent();

				commit.setSha(sSha);

				NodeList nList = element.getElementsByTagName("commit");

				for (int temp = 0; temp < nList.getLength(); temp++) {

					Node nNode = nList.item(temp);

					// System.out.println("\nCurrent Element :" +
					// nNode.getNodeName());

					Element eElement = (Element) nNode;

					NodeList nlCommit = eElement.getElementsByTagName("author");

					for (int i = 0; i < nlCommit.getLength(); i++) {

						Element n = (Element) nlCommit.item(i);

						s = n.getElementsByTagName("date").item(0)
								.getTextContent();
						s = s.substring(0, 10);

						data = df.parse(s);

						commit.setDate(data);
						commit.setNome(n.getElementsByTagName("name").item(0)
								.getTextContent());

					}

					commit.setMessage(eElement.getElementsByTagName("message")
							.item(0).getTextContent());

					commit.setProject(pr);
					contemAutor = false;

					for (int j = 0; j < commits.size(); j++) {
						if (commit.getNome().equals(commits.get(j).getNome())) {
							commits.get(j).addCommit();
							contemAutor = true;
						}
					}

					if (!contemAutor)
						commits.add(commit);

				}
			}

			// new pibicDAO().inserirCommit(commitsAux);

			// p.criarGrafo(commits, project);
			return commits;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}

	public void criarGrafo(List<Commit> commits, Project project) {
		// Graph<V, E> where V is the type of the vertices
		// and E is the type of the edges

		// Note that we can use the same nodes and edges in two different
		// graphs.
		// Graph<String, MyLink> g2 = new SparseMultigraph<String, MyLink>();
		Graph<String, MyLink> g2 = new SparseMultigraph<String, MyLink>();

		g2.addVertex(project.getName());

		for (int i = 0; i < commits.size(); i++) {
			g2.addVertex(commits.get(i).getNome());
			g2.addEdge(new MyLink(commits.get(i).getQntCommits()),
					commits.get(i).getNome(), commits.get(i).getProject()
							.getName());

		}

		// Layout<String, MyLink> layout = new KKLayout<String, MyLink>(g2);
		Layout<String, MyLink> layout = new FRLayout<String, MyLink>(g2);
		// Layout<String, MyLink> layout = new CircleLayout<String, MyLink>(g2);
		layout.setSize(new Dimension(1020, 980)); // sets the initial size of
													// the
													// space
		// The BasicVisualizationServer<V,E> is parameterized by the edge types
		vv = new VisualizationViewer<String, MyLink>(layout);
		vv.setPreferredSize(new Dimension(1920, 1080)); // Sets the viewing area
														// size

		JFrame frame = new JFrame("Simple Graph View");

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Transformer<String, Paint> vertexPaint = new Transformer<String, Paint>() {

			@Override
			public Paint transform(String arg0) {
				// TODO Auto-generated method stub
				return Color.WHITE;
			}
		};

		Transformer<String, Shape> vertexSize = new Transformer<String, Shape>() {
			public Shape transform(String i) {
				Ellipse2D circle = new Ellipse2D.Double(-80, -80, 160, 160);
				// in this case, the vertex is twice as large
				return circle;
			}
		};

		vv.getRenderContext().setVertexShapeTransformer(vertexSize);

		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		// vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
		vv.getRenderContext().setVertexLabelTransformer(
				new ToStringLabeller<String>());
		vv.getRenderContext().setEdgeLabelTransformer(
				new ToStringLabeller<MyLink>());

		vv.addGraphMouseListener(new GraphMouseListener<String>() {

			@Override
			public void graphClicked(String v, MouseEvent me) {
				if (me.getButton() == MouseEvent.BUTTON1
						&& me.getClickCount() == 2) {
					System.out.println("Double clicked " + v);

				}
				me.consume();
			}

			@Override
			public void graphPressed(String arg0, MouseEvent arg1) {
				// TODO Auto-generated method stub
				vv.getRenderer().getVertexLabelRenderer().setPositioner(null);
			}

			@Override
			public void graphReleased(String arg0, MouseEvent arg1) {
				// TODO Auto-generated method stub

			}
		});

		// for zoom:
		vv.getRenderContext().getMultiLayerTransformer()
				.getTransformer(Layer.LAYOUT).setScale(1, 1, vv.getCenter());
		// for out:
		vv.getRenderContext().getMultiLayerTransformer()
				.getTransformer(Layer.VIEW).setScale(1, 1, vv.getCenter());

		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);

		JPanel panel = new GraphZoomScrollPane(vv);
		frame.add(panel);

		DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();

		vv.setGraphMouse(graphMouse);

		JComboBox modeBox = graphMouse.getModeComboBox();
		modeBox.addItemListener(graphMouse.getModeListener());
		graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);

		// frame.getContentPane().add(vv);
		frame.pack();
		frame.setVisible(true);
	}

	public void criarGrafoUser(List<Project> projects) {
		// Graph<V, E> where V is the type of the vertices
		// and E is the type of the edges

		// Note that we can use the same nodes and edges in two different
		// graphs.
		// Graph<String, MyLink> g2 = new SparseMultigraph<String, MyLink>();
		Graph<String, Integer> g2 = new SparseMultigraph<String, Integer>();

		g2.addVertex(projects.get(0).getUser().getUser());

		for (int i = 0; i < projects.size(); i++) {
			g2.addVertex(projects.get(i).getName());
			g2.addEdge(i, projects.get(i).getUser().getUser(), projects.get(i)
					.getName());

		}

		// Layout<String, MyLink> layout = new KKLayout<String, MyLink>(g2);
		Layout<String, Integer> layout = new FRLayout<String, Integer>(g2);
		// Layout<String, MyLink> layout = new CircleLayout<String, MyLink>(g2);
		layout.setSize(new Dimension(1020, 980)); // sets the initial size of
													// the
													// space
		// The BasicVisualizationServer<V,E> is parameterized by the edge types
		vs = new VisualizationViewer<String, Integer>(layout);
		vs.setPreferredSize(new Dimension(1920, 1080)); // Sets the viewing area
														// size

		JFrame frame = new JFrame("Simple Graph View");

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Transformer<String, Paint> vertexPaint = new Transformer<String, Paint>() {

			@Override
			public Paint transform(String arg0) {
				// TODO Auto-generated method stub
				return Color.WHITE;
			}
		};

		Transformer<String, Shape> vertexSize = new Transformer<String, Shape>() {
			public Shape transform(String i) {
				Ellipse2D circle = new Ellipse2D.Double(-30, -30, 60, 60);
				// in this case, the vertex is twice as large
				return circle;
			}
		};

		vs.getRenderContext().setVertexShapeTransformer(vertexSize);

		vs.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		// vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
		vs.getRenderContext().setVertexLabelTransformer(
				new ToStringLabeller<String>());

		vs.addGraphMouseListener(new GraphMouseListener<String>() {

			@Override
			public void graphClicked(String v, MouseEvent me) {
				if (me.getButton() == MouseEvent.BUTTON1
						&& me.getClickCount() == 2) {
					System.out.println("Double clicked " + v);

				}
				me.consume();
			}

			@Override
			public void graphPressed(String arg0, MouseEvent arg1) {
				// TODO Auto-generated method stub
				vs.getRenderer().getVertexLabelRenderer().setPositioner(null);
			}

			@Override
			public void graphReleased(String arg0, MouseEvent arg1) {
				// TODO Auto-generated method stub

			}
		});

		// for zoom:
		vs.getRenderContext().getMultiLayerTransformer()
				.getTransformer(Layer.LAYOUT).setScale(1, 1, vs.getCenter());
		// for out:
		vs.getRenderContext().getMultiLayerTransformer()
				.getTransformer(Layer.VIEW).setScale(1, 1, vs.getCenter());

		vs.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);

		JPanel panel = new GraphZoomScrollPane(vs);
		frame.add(panel);

		DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();

		vs.setGraphMouse(graphMouse);

		JComboBox modeBox = graphMouse.getModeComboBox();
		modeBox.addItemListener(graphMouse.getModeListener());
		graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);

		// frame.getContentPane().add(vv);
		frame.pack();
		frame.setVisible(true);
	}

	public void lerJson() {
		try {
			URL myURL = new URL(
					"https://api.github.com/repos/pedro-banali/teste/commits");
			HttpURLConnection connection = (HttpURLConnection) myURL
					.openConnection();
			// connection.connect();

			// connection.setDoOutput(true);
			// connection.setDoInput(true);
			connection.setRequestMethod("GET");
			//
			// System.out.println(connection.getResponseMessage());

			BufferedReader br = new BufferedReader(new InputStreamReader(
					myURL.openStream()));
			StringBuffer strTemp = new StringBuffer();
			String strTemp1 = "";
			while (null != (strTemp1 = br.readLine())) {
				strTemp.append(strTemp1);
			}

			//
			String myName = strTemp.substring(1);

			myName = myName.replace(myName.substring(myName.length() - 1), "");

			System.out.println(myName.charAt(0) + " "
					+ myName.substring(myName.length() - 1));

			JSONObject json = new JSONObject(myName);
			String xml = XML.toString(json);
			System.out.println(xml);

			// BufferedReader br = new BufferedReader(new FileReader(
			// "./data/data2.json"));

			// while (br.ready()) {
			// String linha = br.readLine();
			// // System.out.println(linha);
			// strTemp.append(linha);
			// }
			br.close();

		} catch (MalformedURLException e) {
			// new URL() failed
			// ...
		} catch (IOException e) {
			// openConnection() failed
			// ...
		}
	}

	class MyLink {
		double weight;
		int id;

		public MyLink(double weight) {
			this.id = edgeCount++;
			this.weight = weight;
		}

		public String toString() {
			return "" + weight;
		}

	}

}
