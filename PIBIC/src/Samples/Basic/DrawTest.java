//package Samples.Basic;
//
///*
// This program is a part of the companion code for Core Java 8th ed.
// (http://horstmann.com/corejava)
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//
//import java.awt.EventQueue;
//import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.geom.Ellipse2D;
//import java.awt.geom.Line2D;
//import java.awt.geom.Rectangle2D;
//
//import javax.swing.JComponent;
//import javax.swing.JFrame;
//
//import pucpr.br.ppgia.pibic.Principal.MyLink;
//import edu.uci.ics.jung.graph.Graph;
//import edu.uci.ics.jung.graph.SparseMultigraph;
//
///**
// * @version 1.32 2007-04-14
// * @author Cay Horstmann
// */
//public class DrawTest {
//  public static void main(String[] args) {
//    EventQueue.invokeLater(new Runnable() {
//      public void run() {
//        DrawFrame frame = new DrawFrame();
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setVisible(true);
//      }
//    });
//  }
//}
//
///**
// * A frame that contains a panel with drawings
// */
//class DrawFrame extends JFrame {
//  public DrawFrame() {
//    setTitle("DrawTest");
//    setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
//
//    // add panel to frame
//
//    DrawComponent component = new DrawComponent();
//    add(component);
//  }
//
//  public static final int DEFAULT_WIDTH = 400;
//
//  public static final int DEFAULT_HEIGHT = 400;
//}
//
///**
// * A component that displays rectangles and ellipses.
// */
//class DrawComponent extends JComponent {
//  public void paintComponent(Graphics g) {
//    Graphics2D g2 = (Graphics2D) g;
//
//    // draw a rectangle
//
//    Graph<String, Integer> g2 = new SparseMultigraph<String, MyLink>();
//
//	g2.addVertex(project);
//    
//    double leftX = 100;
//    double topY = 100;
//    double width = 200;
//    double height = 150;
//
////    Rectangle2D rect = new Rectangle2D.Double(leftX, topY, width, height);
////    g2.draw(rect);
////
////    // draw the enclosed ellipse
////
//    Ellipse2D ellipse = new Ellipse2D.Double();
////    ellipse.setFrame(rect);
//    g2.draw(ellipse);
////
////    // draw a diagonal line
////
////    g2.draw(new Line2D.Double(leftX, topY, leftX + width, topY + height));
//
//    // draw a circle with the same center
//
////    double centerX = rect.getCenterX();
////    double centerY = rect.getCenterY();
//    double radius = 150;
//
//    Ellipse2D circle = new Ellipse2D.Double(10, 10, 20, 20);
////    circle.setFrameFromCenter(centerX, centerY, centerX + radius, centerY + radius);
//
//
//    g2.draw(circle);
//  }
//}
