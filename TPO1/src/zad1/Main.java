/**
 *
 *  @author Pasławska Aneta S6034
 *
 */

package zad1;


public class Main {

  public static void main(String[] args) {
	  
	  Client c1 = new Client();
	  c1.setVisible(true);
	  Client c2 = new Client();
	  c2.setVisible(true);
/*	  Client c3 = new Client();
	  c3.setVisible(true);*/
	  
	  new Server();
	  
  }
}
