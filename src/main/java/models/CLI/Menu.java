package models.CLI;


import models.Cards.Card;
import models.Deck.Deck;
import models.Heroes.Hero;
import Log.Body;
import Log.Header;
import Log.Log;
import hibernate.Provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Menu {
    public static void Collection() {
        Provider provider = Provider.getInstance();
        provider.beginTransaction();
        List<Header> allHeaders = provider.fetchListFromFiled(Header.class, "username", gameCLI.gameCli.getCurrentPlayer().getName());
        Header playerHead = null;
        for (Header h : allHeaders) {
            if (h.getDeleted_at() == null) {
                playerHead = h;
                break;
            }
        }
        Body collBody = new Body("Collection", "Deck components showed", playerHead);
        collBody.save();
        saveBodyToLog(playerHead, collBody);
        Scanner scanner = new Scanner(System.in);
        for (int i = 0; i < gameCLI.gameCli.getCurrentPlayer().getAllDecks().size(); i++) {
            System.out.println("models/Deck" + "_" + i + " contains " + gameCLI.gameCli.getCurrentPlayer().getChosenPlayerDeck().getChosenCards().size() + " cards, Witch Are these:*");
            for (Card initCards : gameCLI.gameCli.getCurrentPlayer().getAllDecks().get(i).getChosenCards())
                System.out.print(initCards.getName() + " *");

        }
        while (true) {
            System.out.println("\n" + "To continue choose one of these choices: (ls -a -heroes)/ (ls -m -hero)/ (select hero)/ (ls -a -cards)/ (ls -m -cards)/ (ls -n -cards)/ (add card)/ (remove card)/ (exit)/ (exit all)/ (Help)/ (back)");
            ArrayList<String> allHeroes = new ArrayList<>();
            for (Object eachHero : provider.fetchAll(Hero.class))
                allHeroes.add(((Hero) eachHero).getName());
            switch (scanner.nextLine()) {
                case "ls -a -heroes":
                    System.out.println(allHeroes);
                    Body collectionLAHBody = new Body("Collections : Heroes : ls -a -heroes", "All available heroes was showed", playerHead);
                    collectionLAHBody.save();
                    saveBodyToLog(playerHead, collectionLAHBody);
                    break;
                case "ls -m -hero":
                    System.out.println(gameCLI.gameCli.getCurrentPlayer().getChosenPlayerHero().getName());
                    Body collectionLMHBody = new Body("Collections : Heroes : ls -m -heroes", "Selected hero was showed", playerHead);
                    collectionLMHBody.save();
                    saveBodyToLog(playerHead, collectionLMHBody);
                    break;
                case "select hero":
                    while (true) {
                        System.out.println("Enter hero name:");
                        String heroName = scanner.nextLine();
                        boolean flag = false;
                        for (String hName : allHeroes) {
                            if (heroName.equalsIgnoreCase(hName)) {
                                flag = true;
                                break;
                            }
                        }
                        if (flag) {
                            gameCLI.gameCli.getCurrentPlayer().setChosenPlayerHero((Hero) provider.fetch(Hero.class, heroName));
                            gameCLI.gameCli.getCurrentPlayer().load();
                            gameCLI.gameCli.getCurrentPlayer().save();
                            gameCLI.gameCli.getCurrentPlayer().setChosenPlayerDeck(gameCLI.gameCli.getCurrentPlayer().getChosenPlayerHero().getHeroDeck());
                            gameCLI.gameCli.getCurrentPlayer().load();
                            gameCLI.gameCli.getCurrentPlayer().save();
                            List<Card> list = new ArrayList<>();
                            for (Card c : (List<Card>) provider.fetchAll(Card.class)) {
                                if (c.getHeroClass().equals(gameCLI.gameCli.getCurrentPlayer().getChosenPlayerHero().getName()) || c.getHeroClass().equals("Neutral"))
                                    list.add(c);
                            }
                            gameCLI.gameCli.getCurrentPlayer().setAllPlayerCards(list);
                            gameCLI.gameCli.getCurrentPlayer().save();
                            Body collectionSetHero = new Body("Collections : Heroes : select hero", heroName + " was selected", playerHead);
                            collectionSetHero.save();
                            saveBodyToLog(playerHead, collectionSetHero);
                            break;
                        } else {
                            System.out.println("Invalid Hero name!!! ");
                        }
                    }
                    break;
                case "ls -a -cards":
                    for (Card c : gameCLI.gameCli.getCurrentPlayer().getAllPlayerCards())
                        System.out.println(c.getName());
                    Body collectionLAC = new Body("Collections : Cards : ls -a -cards", " see all your cards", playerHead);
                    collectionLAC.save();
                    saveBodyToLog(playerHead, collectionLAC);
                    break;
                case "ls -m -cards":
                    System.out.println("Hero name :" + gameCLI.gameCli.getCurrentPlayer().getChosenPlayerHero().getName() + " with : " + gameCLI.gameCli.getCurrentPlayer().getChosenPlayerDeck().getChosenCards().size() + " cards.");
                    for (Card c : gameCLI.gameCli.getCurrentPlayer().getChosenPlayerDeck().getChosenCards()) {
                        System.out.println(c.getName());
                    }
                    Body collectionLMC = new Body("Collections : Cards : ls -m -cards", " see your selected deck", playerHead);
                    collectionLMC.save();
                    saveBodyToLog(playerHead, collectionLMC);
                    break;
                case "ls -n -cards":
                    for (Card c : gameCLI.gameCli.getCurrentPlayer().getAllPlayerCards())
                        if (!gameCLI.gameCli.getCurrentPlayer().getChosenPlayerDeck().getChosenCards().contains(c))
                            System.out.println(c.getName());
                    Body collectionLNC = new Body("Collections : Cards : ls -n -cards", " see cards witch are not in your selected deck, but available to add to your deck", playerHead);
                    collectionLNC.save();
                    saveBodyToLog(playerHead, collectionLNC);
                    break;
                case "add card":
                    System.out.println("Enter a card name: ");
                    String cardName = scanner.nextLine();
                    int ability = 0;
                    Card card = (Card) provider.fetch(Card.class, cardName);
                    if (cardName.equalsIgnoreCase("exit all")) {
                        Body collectionE = new Body("Collections : Cards : exit all", " log out", playerHead);
                        collectionE.save();
                        saveBodyToLog(playerHead, collectionE);
                        provider.commit();
                        System.exit(0);
                    }
                    if (cardName.equalsIgnoreCase("exit")) {
                        Body collectionE = new Body("Collections : Cards : exit", " log out", playerHead);
                        collectionE.save();
                        saveBodyToLog(playerHead, collectionE);
                        provider.commit();
                        gameCLI.Start();
                    }
                    if (card != null) {
                        for (Card c : gameCLI.gameCli.getCurrentPlayer().getChosenPlayerDeck().getChosenCards())
                            if (c.getName().equalsIgnoreCase(cardName))
                                ability++;

                        if (ability < 2 && gameCLI.gameCli.getCurrentPlayer().getAllPlayerCards().contains(card) && gameCLI.gameCli.getCurrentPlayer().getChosenPlayerDeck().getChosenCards().size() < 15) {
                            List<Card> Arr = gameCLI.gameCli.getCurrentPlayer().getChosenPlayerDeck().getChosenCards();
                            Arr.add((Card) provider.fetch(Card.class, cardName));
                            Deck d = gameCLI.gameCli.getCurrentPlayer().getChosenPlayerDeck();
                            d.save();
                            d.setChosenCards(Arr);
                            gameCLI.gameCli.getCurrentPlayer().setChosenPlayerDeck(d);
                            gameCLI.gameCli.getCurrentPlayer().save();
                            Body collectionAC = new Body("Collections : Cards : add card", " an available card added successfully to your deck", playerHead);
                            collectionAC.save();
                            saveBodyToLog(playerHead, collectionAC);
                            break;
                        }
                        if (ability >= 2) {
                            System.out.println("It is\'nt possible, because there are already two of this card in deck.");
                            Body collectionAC = new Body("Collections : Cards : add card", " chosen card could\'nt be added, because there were already two of this card in deck.", playerHead);
                            collectionAC.save();
                            saveBodyToLog(playerHead, collectionAC);
                        }
                        if (!gameCLI.gameCli.getCurrentPlayer().getAllPlayerCards().contains(card)) {
                            System.out.println("This card is not valid. Try another card...");
                            Body collectionAC = new Body("Collections : Cards : add card", " chosen card could\'nt be added, because it was\'nt valid.", playerHead);
                            collectionAC.save();
                            saveBodyToLog(playerHead, collectionAC);
                        }
                    } else {
                        System.out.println("Invalid input!!!");
                        Body collectionAC = new Body("Collections : Cards : add card", " Invalid input", playerHead);
                        collectionAC.save();
                        saveBodyToLog(playerHead, collectionAC);
                    }

                    break;
                case "remove card":
                    System.out.println("Enter a card name: ");
                    String cardName1 = scanner.nextLine();
                    int ability1 = 0;
                    Card card1 = (Card) provider.fetch(Card.class, cardName1);
                    if (cardName1.equalsIgnoreCase("exit all")) {
                        Body collectionE = new Body("Collections : Cards : exit all", " log out", playerHead);
                        collectionE.save();
                        saveBodyToLog(playerHead, collectionE);
                        provider.commit();
                        System.exit(0);
                    } else if (cardName1.equalsIgnoreCase("exit")) {
                        Body collectionE = new Body("Collections : Cards : exit", " log out", playerHead);
                        collectionE.save();
                        saveBodyToLog(playerHead, collectionE);
                        provider.commit();
                        gameCLI.Start();
                    } else if (card1 != null) {
                        for (Card c : gameCLI.gameCli.getCurrentPlayer().getChosenPlayerDeck().getChosenCards())
                            if (c.getName().equalsIgnoreCase(cardName1))
                                ability1++;

                        if (ability1 > 0) {
                            if (gameCLI.gameCli.getCurrentPlayer().getChosenPlayerDeck().getChosenCards().size() - 1 >= 10) {
                                provider.commit();
                                provider.beginTransaction();
                                List<Card> Arr = gameCLI.gameCli.getCurrentPlayer().getChosenPlayerDeck().getChosenCards();
                                Arr.remove(card1);
                                Deck d = gameCLI.gameCli.getCurrentPlayer().getChosenPlayerDeck();
                                d.setChosenCards(Arr);
                                d.save();
                                gameCLI.gameCli.getCurrentPlayer().setChosenPlayerDeck(d);
                                gameCLI.gameCli.getCurrentPlayer().save();
                                Body collectionRC = new Body("Collections : Cards : remove card", " chosen card removed successfully.", playerHead);
                                collectionRC.save();
                                saveBodyToLog(playerHead, collectionRC);
                                provider.commit();
                                provider.beginTransaction();
                            } else {
                                System.out.println("This is not possible because deck capacity might be less than 10 cards.");
                                Body collectionRC = new Body("Collections : Cards : remove card", " chosen card could\'nt be removed, because deck capacity might be less than 10 cards.", playerHead);
                                collectionRC.save();
                                saveBodyToLog(playerHead, collectionRC);
                            }
                        } else {
                            System.out.println("This card is not available in deck.");
                            Body collectionRC = new Body("Collections : Cards : remove card", " chosen card could\'nt be removed, because this card is not available in deck.", playerHead);
                            collectionRC.save();
                            saveBodyToLog(playerHead, collectionRC);
                        }
                    } else {
                        System.out.println("Invalid input!!!");
                        Body collectionRC = new Body("Collections : Cards : remove card", " Invalid input.", playerHead);
                        collectionRC.save();
                        saveBodyToLog(playerHead, collectionRC);
                    }
                    break;
                case "exit":
                    Body collectionE = new Body("Collections : Cards : exit", " log out", playerHead);
                    collectionE.save();
                    saveBodyToLog(playerHead, collectionE);
                    provider.commit();
                    gameCLI.Start();
                    break;
                case "exit all":
                    Body collectionEA = new Body("Collections : Cards : exit all", " log out", playerHead);
                    collectionEA.save();
                    saveBodyToLog(playerHead, collectionEA);
                    provider.commit();
                    System.exit(0);
                    break;
                case "Help":
                    Body collectionH = new Body("Collections : Help", " help panel showed", playerHead);
                    collectionH.save();
                    saveBodyToLog(playerHead, collectionH);
                    System.out.println("*To see all available heroes, enter keyword [ls -a -heroes].\n " +
                            "*To see the selected hero, enter keyword [ls -m -hero].\n " +
                            "*To select your hero, enter keyword [select hero].\n " +
                            "*To see all your cards, enter keyword [ls -a -cards].\n" +
                            "*To see your selected deck, enter keyword [ls -m -cards].\n" +
                            "*To see cards witch are not in your selected deck, but available to add to your deck, enter keyword [ls -n -cards].\n" +
                            "*To add an available card to your deck, enter keyword [add card].\n" +
                            "*To remove a card from your deck, enter keyword[remove card].\n " +
                            "*To return back to main menu, enter keyword [back].");
                    provider.commit();
                    Collection();
                    break;
                case "back":
                    provider.commit();
                    gameCLI.selectMenu();
                    break;
                default:
                    System.out.println("Invalid input!!! Check Help box.");
                    break;

            }
            provider.commit();
        }
    }

    public static void Store() {

        while (true) {
            Provider provider = Provider.getInstance();
            provider.beginTransaction();
            Scanner scanner = new Scanner(System.in);
            List<Header> allHeaders = provider.fetchListFromFiled(Header.class, "username", gameCLI.gameCli.getCurrentPlayer().getName());
            Header playerHead = null;
            for (Header h : allHeaders)
                if (h.getDeleted_at() == null)
                    playerHead = h;
            System.out.println("\n" + "Choose one of these choices: (buy card)/ (wallet)/ (sell card)/ (ls -s)/ (ls -b)/ (exit)/ (exit all)/ (Help)/ (back)");
            Body Store = new Body("Store", " Store menu showed.", playerHead);
            Store.save();
            saveBodyToLog(playerHead, Store);
            switch (scanner.nextLine()) {
                case "buy card":
                    System.out.println("Enter the card name that you want to buy:");
                    String cardName = scanner.nextLine();
                    Card card = (Card) provider.fetch(Card.class, cardName);
                    if (card != null && gameCLI.gameCli.getCurrentPlayer().getMoney() >= card.getRarity().getPrice() && gameCLI.gameCli.getCurrentPlayer().getAvailableHeroes().contains(card.getHeroClass())) {
                        gameCLI.gameCli.getCurrentPlayer().setMoney(gameCLI.gameCli.getCurrentPlayer().getMoney() - card.getRarity().getPrice());
                        List<Card> cardList = gameCLI.gameCli.getCurrentPlayer().getAllPlayerCards();
                        cardList.add(card);
                        gameCLI.gameCli.getCurrentPlayer().setAllPlayerCards(cardList);
                        gameCLI.gameCli.getCurrentPlayer().save();
                        Body StoreBC = new Body("Store : buy card ", " card bought successfully:)", playerHead);
                        StoreBC.save();
                        saveBodyToLog(playerHead, StoreBC);
                    } else if (gameCLI.gameCli.getCurrentPlayer().getMoney() < card.getRarity().getPrice()) {
                        Body StoreBC = new Body("Store : buy card ", " card could\'nt be bought, because You don\'t have enough money:(", playerHead);
                        StoreBC.save();
                        saveBodyToLog(playerHead, StoreBC);
                        System.out.println("You don\'t have enough money:(");
                    } else {
                        Body StoreBC = new Body("Store : buy card ", " card could\'nt be bought, because Cart is not valid", playerHead);
                        StoreBC.save();
                        saveBodyToLog(playerHead, StoreBC);
                        System.out.println("Cart is not valid!!!");
                    }
                    break;
                case "wallet":
                    Body StoreW = new Body("Store : Wallet ", " money in wallet showed", playerHead);
                    StoreW.save();
                    saveBodyToLog(playerHead, StoreW);
                    System.out.println("You have " + gameCLI.gameCli.getCurrentPlayer().getMoney() + (gameCLI.gameCli.getCurrentPlayer().getMoney() > 30 ? " (: " : " ): ") + "money in your wallet.");
                    break;
                case "sell card":
                    System.out.println("Enter the card name that you want to sell:");
                    String cardName1 = scanner.nextLine();
                    Card card1 = (Card) provider.fetch(Card.class, cardName1);
                    boolean flag = false;
                    for (Deck deck : gameCLI.gameCli.getCurrentPlayer().getAllDecks())
                        if (deck.getChosenCards().contains(card1)) {
                            flag = true;
                            break;
                        }
                    if (card1 != null && gameCLI.gameCli.getCurrentPlayer().getAllPlayerCards().contains(card1) && !flag) {
                        gameCLI.gameCli.getCurrentPlayer().setMoney(gameCLI.gameCli.getCurrentPlayer().getMoney() + card1.getRarity().getPrice());
                        List<Card> cardList = gameCLI.gameCli.getCurrentPlayer().getAllPlayerCards();
                        cardList.remove(card1);
                        gameCLI.gameCli.getCurrentPlayer().setAllPlayerCards(cardList);
                        gameCLI.gameCli.getCurrentPlayer().save();
                        Body StoreSC = new Body("Store : sell card ", " card sold successfully:)", playerHead);
                        StoreSC.save();
                        saveBodyToLog(playerHead, StoreSC);
                        System.out.println("card sold successfully:)");
                    } else if (card1 == null) {
                        Body StoreSC = new Body("Store : sell card ", " Cart is not valid:)", playerHead);
                        StoreSC.save();
                        saveBodyToLog(playerHead, StoreSC);
                        System.out.println("Cart is not valid!!!");
                    } else if (flag) {
                        Body StoreSC = new Body("Store : sell card ", " Cart could\'nt been sold, because Current card is a member of some your decks.", playerHead);
                        StoreSC.save();
                        saveBodyToLog(playerHead, StoreSC);
                        System.out.println("Current card is a member of some your decks, then you can not sell it.");
                    }
                    break;
                case "ls -s":
                    Body StoreLS = new Body("Store : ls -s ", " All cards that you can sell were showed.", playerHead);
                    StoreLS.save();
                    saveBodyToLog(playerHead, StoreLS);
                    boolean flag1 = false;
                    System.out.println("All cards that you can sell are: * ");
                    for (Card card2 : gameCLI.gameCli.getCurrentPlayer().getAllPlayerCards())
                        for (Deck deck : gameCLI.gameCli.getCurrentPlayer().getAllDecks())
                            if (!deck.getChosenCards().contains(card2)) {
                                System.out.print(card2.getName() + " *");
                                flag1 = true;
                            }
                    if (!flag1)
                        System.out.print(" No card is available to sell...*");
                    break;
                case "ls -b":
                    Body StoreLB = new Body("Store : ls -b ", " All cards that you can buy were showed.", playerHead);
                    StoreLB.save();
                    saveBodyToLog(playerHead, StoreLB);
                    boolean flag2 = false;
                    System.out.println("All cards that you can buy are: *");
                    List<Card> AllExistedCards = provider.fetchAll(Card.class);
                    for (Card card3 : AllExistedCards) {
                        if ((card3.getHeroClass().equalsIgnoreCase("Neutral")|| (gameCLI.gameCli.getCurrentPlayer().getAvailableHeroes().contains(card3.getHeroClass()))) && gameCLI.gameCli.getCurrentPlayer().getMoney() >= card3.getRarity().getPrice()) {
                            System.out.println(card3.getName() + " *");
                            flag2 = true;
                        }
                    }
                    if (!flag2)
                        System.out.print(" No card is available to buy...*");
                    break;
                case "exit":
                    Body StoreE = new Body("Store : exit ", " Log out.", playerHead);
                    StoreE.save();
                    saveBodyToLog(playerHead, StoreE);
                    gameCLI.Start();
                    break;
                case "exit all":
                    Body StoreEA = new Body("Store : exit all ", " Log out.", playerHead);
                    StoreEA.save();
                    saveBodyToLog(playerHead, StoreEA);
                    System.exit(0);
                    break;
                case "Help":
                    Body StoreH = new Body("Store : Help ", " Help panel was showed.", playerHead);
                    StoreH.save();
                    saveBodyToLog(playerHead, StoreH);
                    System.out.println("*To buy a card from Store, enter keyword [buy card].\n " +
                            "*To sell a card from Store, enter keyword [sell card].\n " +
                            "*To check your money in wallet, enter keyword [wallet].\n " +
                            "*To see all cards witch you can sell, enter keyword [ls -s].\n" +
                            "*To see all cards witch you can buy, enter keyword [ls -b].\n" +
                            "*To return back to main menu, enter keyword [back].");
                    Store();
                    break;
                case "back":
                    gameCLI.selectMenu();
                    break;
                default:
                    System.out.println("Invalid input!!! Check Help box.");
                    break;

            }
            provider.commit();
        }
    }

    public static void saveBodyToLog(Header playerHead, Body playerBody) {
        Provider provider = Provider.getInstance();
        List logs = provider.fetchListForLog(Log.class, "header", playerHead);
        Log playerLog = null;
        for (Object l : logs)
            if (((Log) l).getHeader().getDeleted_at() == null)
                playerLog = (Log) l;
        List<Body> B = playerLog.getAllBody();
        B.add(playerBody);
        playerLog.setAllBody(B);
    }
}
