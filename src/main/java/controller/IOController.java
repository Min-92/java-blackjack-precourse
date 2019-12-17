package controller;

import domain.blackjack.Rule;
import domain.card.Card;
import domain.user.Dealer;
import domain.user.Player;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class IOController {

  private static final String HIT = "HIT";
  private static final String STAY = "STAY";

  private static void askNames() {
    System.out.println("게임에 참여할 사람의 이름을 입력하세요.(쉼표기준으로 분리)");
  }

  private static String inputNames() {
    Scanner scanner = new Scanner(System.in);
    String names = scanner.nextLine();
    return names;
  }

  private static String[] parseNames(String names) {
    String[] users = names.split(",");

    for (int i = 0; i < users.length; i++) {
      users[i] = users[i].trim();
    }

    return users;
  }

  private static void validateName(String name) throws Exception {
    if (name.length() == 0) {
      throw new Exception("이름은 공백으로 이루어질 수 없습니다.");
    }
  }

  private static void validateNames(String[] names) throws Exception {
    if (names.length == 0) {
      throw new Exception("이름이 유효하지 않습니다.");
    }

    if (names.length > 6) {
      throw new Exception("6명까지 플레이가 가능합니다.");
    }

    for (String name : names) {
      validateName(name);
    }
  }

  private static String[] getUsersAgain(String message) {
    System.out.println(message);
    return getUsers();
  }

  public static String[] getUsers() {
    askNames();
    String[] users = parseNames(inputNames());

    try {
      validateNames(users);

    } catch (Exception e) {
      return getUsersAgain(e.getMessage());
    }

    return users;
  }

  public static double[] getBetting(String[] users) {
    double[] betting = new double[users.length];

    for (int i = 0; i < users.length; i++) {
      betting[i] = getBetting(users[i]);
    }

    return betting;
  }

  private static void askBettingMoney(String name) {
    System.out.println(name + "의 베팅 금액은?");
  }

  private static void validateMoney(int money) throws Exception {
    if (money <= 0) {
      throw new Exception("금액이 올바르지 않습니다.");
    }

    if (money >= 10000000) {
      throw new Exception("천만 이하로만 가능합니다.");
    }
  }

  private static double inputMoney() {
    try {
      Scanner scanner = new Scanner(System.in);
      int money = scanner.nextInt();

      validateMoney(money);

      return money;
    } catch (Exception e) {
      System.out.println("금액이 올바르지 않습니다($1 이상, $10,000,000 미만 가능)");
      return inputMoney();
    }
  }

  private static double getBetting(String user) {
    askBettingMoney(user);
    double money = inputMoney();

    return money;
  }

  public static void printDealCardToUser(String name) {
    System.out.println(name + "이(가) 한장 받습니다...");
  }

  public static void printDealCardToDealer() {
    System.out.println("딜러가 한장 받습니다...");
  }

  private static void printCard(Card card) {
    System.out.print("[");
    System.out.print(card.getSuit());
    System.out.print(" ");
    System.out.print(card.getSymbol());
    System.out.print("] ");
  }

  private static void printReverseCard(Card card) {
    System.out.print("[   ]");
  }

  public static void printHandsOfDealer(Dealer dealer, boolean coverCard) {
    List<Card> cards = dealer.getHands();

    System.out.print("딜러 : ");
    if (coverCard) {
      printCard(cards.get(0));
      printReverseCard(cards.get(1));
      System.out.println();
      return;
    }
    for (int i = 0; i < cards.size(); i++) {
      printCard(cards.get(i));
    }
    printScore(cards);
    System.out.println();


  }

  public static void printHandsOfPlayer(Player player) {
    List<Card> cards = player.getHands();
    System.out.print(player.getName() + " : ");
    for (int i = 0; i < cards.size(); i++) {
      printCard(cards.get(i));
    }
    printScore(cards);
    System.out.println();
  }

  public static void askAction(String name) {
    System.out.println(name + "은(는) 한장을 더 받으시겠습니까?(Y or N)");
  }

  private static String inputAction() {
    Scanner scanner = new Scanner(System.in);
    return scanner.nextLine();
  }

  public static String getAction() {
    String input = inputAction();

    try {
      validateAction(input);

      return decideAction(input);
    } catch (Exception e) {

      System.out.println(e.getMessage());
      return getAction();
    }
  }

  private static String decideAction(String input) {
    if (input.equals("Y") || input.equals("y")) {
      return HIT;
    }

    return STAY;
  }

  private static void validateAction(String input) throws Exception {
    if (!input.equals("Y") && !input.equals("y") && !input.equals("n") && !input.equals("N")) {
      throw new Exception("입력이 올바르지 않습니다.");
    }
  }

  private static void printScore(List<Card> cards) {
    int score = Rule.getScore(cards);
    System.out.print("점수 : " + score);
  }

  public static void printGameResult(Dealer dealer, ArrayList<Player> players) {
    System.out.println("-------- 게임 결과 --------");
    printHandsOfDealer(dealer, false);

    for (Player player : players) {
      printHandsOfPlayer(player);
    }
    System.out.println();
  }

  private static void printDealerReward(ArrayList<Double> scoreBoard) {
    double reward = 0;
    for (int i = 0; i < scoreBoard.size(); i++) {
      reward -= scoreBoard.get(i);
    }
    System.out.printf("딜러 : %.0f\n", reward);
  }

  private static void printPlayerReward(Player player, double score) {
    System.out.printf(player.getName()+" : %.0f\n",score);
  }

  public static void printRewardResult(ArrayList<Player> players, ArrayList<Double> scoreBoard) {
    System.out.println("-------- 최종 수익 --------");
    printDealerReward(scoreBoard);

    for (int i = 0; i < players.size(); i++) {
      printPlayerReward(players.get(i), scoreBoard.get(i));
    }
  }


}
