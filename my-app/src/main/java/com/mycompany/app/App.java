package com.mycompany.app;
// TODO

/**
 * Hello world!
 *
 */
public class App {
  /**
   * Constructor
   *
   * @param name
   */
  public App(String name) {
    // TODO do something useful
    System.out.println("hello");
  }

  /**
   * name
   *
   * @return name
   */
  @Deprecated
  public String getName() {
    return "hello";
  }

  public static void main(String[] args) {
    System.out.println("Hello World!");
    System.out.println("Hello World! 2");
    System.out.println("Hello World! 3");
    System.out.println("Hello World! 4");
    System.out.println("Hello World! 5");
    System.out.println("Hello World! 6");
    System.out.println("Hello World! 7");
    System.out.println("Hello World! 8");
  }

  /**
   * some JavaDoc
   */
  public String foo() {
    return getName();
  }
}
