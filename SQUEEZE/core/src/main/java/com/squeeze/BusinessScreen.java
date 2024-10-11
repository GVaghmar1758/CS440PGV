package com.squeeze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/** Business screen for buying and selling lemonade. */
public class BusinessScreen implements Screen {
    private Stage stage;
    private BitmapFont customFont;
//    private Skin skin;

    // TODO: replace with GUI
    // Followings are used before starting the day
    private Label appNameLabel;
    private TextButton startButton;
    // Followings are used in selling stage
    private TextButton sellButton;
    private TextButton makeButton;
    private Label orderLabel;
    private Label cashLabel;
    private Label inventoryLabel;
    private Label customerLabel;
    // Followings are used in weekend management stage
    private TextButton stockUpButton;
    private TextButton finishButton;
    private Label menuLabel;
    private Label predictionLabel;
    private TextButton priceUpButton;
    private TextButton priceDownButton;
    // Followings are used in weekend statistics
    private TextButton okButton;
    private Label statisticsLabel;

    // Other variables
    private boolean dayStarted = false;
    private Random random;
    private boolean endOfWeek = false;
    private int currentProductID;
    private String currentProductName;
    private int currentProductQuantity;
    private Business business;
    private double cash;
    private int customerCount;
    private AssetManager manager;
    private SpriteBatch spriteBatch;
    private Texture backgroundTexture;
    private Texture gamebgTexture;
    private Image menuImage;

    public BusinessScreen() {
        Sound sound = Gdx.audio.newSound(Gdx.files.internal("bg_sound.mp3"));
        long id = sound.play(1.0f);
        sound.setLooping(id, true); // keeps the sound looping

        // Generate custom font using FreeTypeFontGenerator
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("MotionControl-Bold.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 32; // Set font size
        parameter.color = Color.WHITE; // Set font color
        BitmapFont customFont = generator.generateFont(parameter);
        generator.dispose(); // Dispose the generator after generating the font
        this.manager = new AssetManager();
        this.business = new Business();
        this.spriteBatch = new SpriteBatch();

        // Load the background image
        backgroundTexture = new Texture(Gdx.files.internal("bg_img.jpg"));
        gamebgTexture = new Texture(Gdx.files.internal("game_bd.jpg"));

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        manager.load("ui_assets\\PNG\\Default\\banner_hanging.png",Texture.class);
        manager.load("ui_assets\\PNG\\Default\\button_grey.png",Texture.class);
        manager.load("game_stat.png",Texture.class);
        manager.load("message_bg.png",Texture.class);
        manager.finishLoading();
        Texture banner = manager.get("ui_assets\\PNG\\Default\\banner_hanging.png");
        Texture buttonbg = manager.get("ui_assets\\PNG\\Default\\button_grey.png");
        Texture menu_state = manager.get("game_stat.png");
        Texture message_bg = manager.get("message_bg.png");

        Image bannerImage = new Image(banner);
        Image buttonImage = new Image(buttonbg);
        Image messageImage = new Image(message_bg);
        menuImage = new Image(menu_state);
        
        bannerImage.setSize(800, 250);
        Label titleOfGame = new Label("Welcome to SQUEEZE!", new Label.LabelStyle(customFont, Color.WHITE));
        bannerImage.setPosition(Gdx.graphics.getWidth()/2 - 350,Gdx.graphics.getHeight() - 350);
        titleOfGame.setPosition(Gdx.graphics.getWidth()/2-300,Gdx.graphics.getHeight() - 250);
        titleOfGame.setFontScale(3);
        
        stage.addActor(bannerImage);
        stage.addActor(titleOfGame);
    
        // Create a drawable for the button background with padding
        NinePatchDrawable buttonDrawable = new NinePatchDrawable(new NinePatch(
            new Texture(Gdx.files.internal("ui_assets\\PNG\\Default\\button_grey.png")), // Your button texture
            10, 10, 10, 10 // Set the padding for the NinePatch (left, right, top, bottom)
        ));
        // Create a button to start the process
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = customFont;
        // Set the button drawables for different states
        buttonStyle.up = buttonDrawable;
        buttonStyle.down = buttonDrawable;
        buttonStyle.checked = buttonDrawable;
        buttonStyle.over = buttonDrawable;

        startButton = new TextButton("Start Game", buttonStyle);
        startButton.setSize(200, 50);
        startButton.setPosition(Gdx.graphics.getWidth() / 2 - startButton.getWidth() / 2, Gdx.graphics.getHeight() / 2 - 20);
        
        messageImage.setSize(1200, 200);
        messageImage.setPosition(Gdx.graphics.getWidth()/2 - 600 , Gdx.graphics.getHeight()/2 - 350);
        stage.addActor(messageImage);
        // Create a label for the game description
        Label.LabelStyle descriptionStyle = new Label.LabelStyle(customFont, Color.TAN); // Use the same custom font for consistency
        Label gameDescription = new Label(
            "In this game, you'll run a lemonade stand! Manage your supplies, set prices, and try to make a profit by selling lemonade to customers.",
            descriptionStyle
        );
        gameDescription.setWrap(true); // Enable text wrapping if the description is long
        gameDescription.setFontScale(1.5f); // Scale the font for better readability
        gameDescription.setAlignment(Align.center); // Center align the text

        // Set the position for the description
        gameDescription.setSize(Gdx.graphics.getWidth() - 100, 200); // Set the width of the label and height for the text box
        gameDescription.setPosition(Gdx.graphics.getWidth()/2 - 575 , Gdx.graphics.getHeight()/2 - 350); // Position it below the start button

        // Add the game description label to the stage
        stage.addActor(gameDescription);

        TextButton quit = new TextButton("Quit", buttonStyle);
        quit.setSize(200, 50);
        quit.setPosition(Gdx.graphics.getWidth() / 2 - quit.getWidth() / 2, Gdx.graphics.getHeight() / 2 - 100);
        quit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        stage.addActor(quit);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean uiCreated = false;
                if (!uiCreated) {
                    messageImage.setVisible(false);
                    gameDescription.setVisible(false);
                    titleOfGame.setVisible(false);
                    bannerImage.setVisible(false);
                    backgroundTexture.dispose();
                    startButton.setVisible(false);
                    
                    uiCreated = true;  // Ensure UI is only created once
                    quit.setPosition(Gdx.graphics.getWidth() / 2 -100, Gdx.graphics.getHeight() / 2 - 375);
        
                    // Create a label for the application name
                    appNameLabel = new Label("DAY 1 OF THE WEEK", new Label.LabelStyle(customFont, Color.WHITE));
                    appNameLabel.setPosition(Gdx.graphics.getWidth() / 2 - appNameLabel.getWidth() / 2, Gdx.graphics.getHeight() / 2 + 50);
                    stage.addActor(appNameLabel);
            
                    startButton = new TextButton("Start Today", buttonStyle );
                    startButton.setSize(200, 50);
                    startButton.setPosition(Gdx.graphics.getWidth() / 2 - startButton.getWidth() / 2, Gdx.graphics.getHeight() / 2 - 20);
                    
                    startButton.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            menuImage.setVisible(true);
                            startGame(buttonStyle, customFont);
                            startDay();
                        }
                    });
                    stage.addActor(startButton);
                }
            }
        });
        stage.addActor(startButton);

    }

    private void startGame(TextButton.TextButtonStyle buttonStyle, BitmapFont customFont) {
        menuImage.setSize(400, 400);
        menuImage.setPosition(Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight() - 550);
        stage.addActor(menuImage);

        // Create a button to sell lemonade
        sellButton = new TextButton("Sell Lemonade", buttonStyle);
        sellButton.setSize(200, 50);
        sellButton.setPosition(Gdx.graphics.getWidth() / 2 - sellButton.getWidth() / 2, Gdx.graphics.getHeight() / 2 - 225);
        sellButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    sellProduct();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        sellButton.setVisible(false); // Initially hidden
        stage.addActor(sellButton);

        // Create a button to make lemonade
        makeButton = new TextButton("Make Lemonade", buttonStyle);
        makeButton.setSize(200,50);
        makeButton.setPosition(Gdx.graphics.getWidth() / 2 - makeButton.getWidth() / 2, Gdx.graphics.getHeight() / 2 - 300);
        makeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    makeProduct();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        makeButton.setVisible(false); // Initially hidden
        stage.addActor(makeButton);

        // Create a button to stock up lemons
        stockUpButton = new TextButton("Refill Lemons", buttonStyle);
        stockUpButton.setPosition(Gdx.graphics.getWidth() / 2 - stockUpButton.getWidth() / 2, Gdx.graphics.getHeight() / 2 - 150);
        stockUpButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    stockUp();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        stockUpButton.setVisible(false); // Initially hidden
        stage.addActor(stockUpButton);

        finishButton = new TextButton("Progress to Next Week", buttonStyle);
        finishButton.setPosition(Gdx.graphics.getWidth() / 2 - finishButton.getWidth() / 2, Gdx.graphics.getHeight() / 2 - 200);
        finishButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                finishPreparation();
            }
        });
        finishButton.setVisible(false); // Initially hidden
        stage.addActor(finishButton);

        customerLabel = new Label("", new Label.LabelStyle(customFont, Color.WHITE));
        customerLabel.setPosition(Gdx.graphics.getWidth() / 2 - customerLabel.getWidth() / 2 + 50, Gdx.graphics.getHeight() / 2 + 175);
        stage.addActor(customerLabel);
        customerLabel.setVisible(false);

        // Label for displaying customer numbers
        orderLabel = new Label("", new Label.LabelStyle(customFont, Color.WHITE));
        orderLabel.setPosition(Gdx.graphics.getWidth() / 2 - orderLabel.getWidth() / 2 + 50, Gdx.graphics.getHeight() / 2 + 150);
        stage.addActor(orderLabel);
        orderLabel.setVisible(false);

        cashLabel = new Label("", new Label.LabelStyle(customFont, Color.WHITE));
        cashLabel.setPosition(Gdx.graphics.getWidth() / 2 - cashLabel.getWidth() / 2 + 50, Gdx.graphics.getHeight() / 2 - 100);
        stage.addActor(cashLabel);
        cashLabel.setVisible(false);

        inventoryLabel = new Label("", new Label.LabelStyle(customFont, Color.WHITE));
        inventoryLabel.setPosition(Gdx.graphics.getWidth() / 2 - inventoryLabel.getWidth() / 2 + 50, Gdx.graphics.getHeight() / 2 + 25);
        stage.addActor(inventoryLabel);
        inventoryLabel.setVisible(false);

        menuLabel = new Label("", new Label.LabelStyle(customFont,Color.WHITE));
        menuLabel.setPosition(75, Gdx.graphics.getHeight() / 2 + 75);
        stage.addActor(menuLabel);
        menuLabel.setVisible(false);

        priceUpButton = new TextButton("+", buttonStyle);
        priceUpButton.setPosition(100, Gdx.graphics.getHeight() / 2 + 50);
        priceUpButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                adjustPrice(currentProductID, 0.5); // should change when we can add new products
            }
        });
        priceUpButton.setVisible(false); // Initially hidden
        stage.addActor(priceUpButton);

        priceDownButton = new TextButton("-", buttonStyle);
        priceDownButton.setPosition(75, Gdx.graphics.getHeight() / 2 + 50);
        priceDownButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                adjustPrice(currentProductID, -0.5); // should change when we can add new products
            }
        });
        priceDownButton.setVisible(false); // Initially hidden
        stage.addActor(priceDownButton);

        predictionLabel = new Label("", new Label.LabelStyle(customFont, Color.WHITE));
        predictionLabel.setPosition(75, Gdx.graphics.getHeight() / 2 + 25);
        stage.addActor(predictionLabel);
        predictionLabel.setVisible(false);

        statisticsLabel = new Label("STATISTICS", new Label.LabelStyle(customFont, Color.WHITE));
        statisticsLabel.setPosition(Gdx.graphics.getWidth() / 2 - statisticsLabel.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        stage.addActor(statisticsLabel);
        statisticsLabel.setVisible(false);

        okButton = new TextButton("OK", buttonStyle);
        okButton.setPosition(Gdx.graphics.getWidth() / 2 - okButton.getWidth() / 2, Gdx.graphics.getHeight() / 2 - 150);
        okButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                weekendManagement();
            }
        });
        okButton.setVisible(false); // Initially hidden
        stage.addActor(okButton);

        random = new Random();
    }
    
    @Override
    public void show() {
        // Prepare your screen here.
    }

    @Override
    public void render(float delta) {
        // Draw your screen here. "delta" is the time since last render in seconds.
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // Begin the spriteBatch to render the background image
        spriteBatch.begin();

        // Draw the background image to cover the whole screen
        spriteBatch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        if (dayStarted) {
            spriteBatch.draw(gamebgTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        // End the spriteBatch
        spriteBatch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
        backgroundTexture.dispose();
        spriteBatch.dispose();
        stage.dispose();
        customFont.dispose();
        manager.dispose();
//        skin.dispose();
    }

    private void hideSellingScreen() {
        sellButton.setVisible(false);
        makeButton.setVisible(false);
        orderLabel.setVisible(false);
        cashLabel.setVisible(false);
        customerLabel.setVisible(false);
        inventoryLabel.setVisible(false);
    }

    private void updateInventory() {
        String inventory = "Drinks:\n";
        ArrayList<Product> products = business.getProducts();
        for (Product product : products) {
            inventory += product.getName() + " x " + product.getQuantity() + "\n";
        }
        inventory += "\nIngredients:\n";
        HashMap<String, Integer> ingredients = business.getIngredients();
        for (String ingredient : ingredients.keySet()) {
            inventory += ingredient + " x " + ingredients.get(ingredient) + "\n";
        }
        inventoryLabel.setText(inventory);
    }

    private void updateMenu() {
        String menu = "Drinks on Menu:\n";
        ArrayList<Product> products = business.getProducts();
        for (Product product : products) {
            menu += product.getName() + ": $" + product.getPrice() + "\n";
        }
        menuLabel.setText(menu);
    }

    private void updatePrediction() {
        String pred = "Prediction for next week:\n";
        ArrayList<Product> products = business.getProducts();
        for (Product product : products) {
            pred += product.getName() + ": " + product.getNextDaySales() + "/day\n";
        }
        predictionLabel.setText(pred);
    }

    private void sellProduct() throws InterruptedException {
        double oldCashValue = business.getCash();
        business.sell(currentProductID, 1);
        cashLabel.setText("Cash: " + business.getCash());
        updateInventory();
        if (business.getCash() > oldCashValue){
            currentProductQuantity--;
        }
        if (currentProductQuantity == 0) {
            orderLabel.setText("Enjoy!");
            takeOrder();
        }
        else {
            orderLabel.setText("Order: " + currentProductQuantity + " x " + currentProductName);
        }
    }

    private void makeProduct() throws InterruptedException {
        business.makeDrink(currentProductID, 1);
        updateInventory();
    }

    private void stockUp() throws InterruptedException {
        business.buyIngredient("lemon", 1);
        cashLabel.setText("Cash: " + business.getCash());
        updateInventory();
    }

    private void adjustPrice(int productID, double amount) {
        double oldPrice = business.getProductPrice(productID);
        if (amount < 0) {
            if (oldPrice > 0) {
                business.adjustPrice(productID, oldPrice + amount);
            }
        }
        else if (oldPrice >= 10) {
            if (amount < 0) {
                business.adjustPrice(productID, oldPrice + amount);
            }
        }
        else {
            business.adjustPrice(productID, oldPrice + amount);
        }
        updateMenu();
        updatePrediction();
    }

    public void startDay(){
        System.out.println("Starting the day...");
        dayStarted = true;

        appNameLabel.setVisible(false);
        startButton.setVisible(false);

        sellButton.setVisible(true);
        makeButton.setVisible(true);

        customerCount = 0;

        // Time elapse, customers come
        if (business.hasRemainingCustomers()){
            orderLabel.setVisible(true);
            cashLabel.setText("Cash: " + business.getCash());
            cashLabel.setVisible(true);
            customerLabel.setVisible(true);
            updateInventory();
            inventoryLabel.setVisible(true);
            takeOrder();
        }
        else {
            cash = business.getCash();
            endOfWeek = business.endToday();
            dayStarted = false;
            if (endOfWeek) {
                // show the statistics
                hideSellingScreen();
                weekendManagement();
            }
            else {
                appNameLabel.setText("DAY " + business.getDay() + " OF THE WEEK");
                appNameLabel.setVisible(true);
                startButton.setVisible(true);
            }
        }
    }

    public void takeOrder() {
        System.out.println("New order coming...");
        if (business.hasRemainingCustomers()){
            customerCount++;
            customerLabel.setText("#" + customerCount + " customer of the day.");
            HashMap<Integer, Integer> order = business.customerOrder();
            for (Integer productID : order.keySet()){
                Integer buyQuantity = order.get(productID);
                System.out.println(buyQuantity);
                currentProductID = productID;
                currentProductQuantity = buyQuantity;
                currentProductName = business.getProductName(productID);
                orderLabel.setText("Order: " + buyQuantity + " x " + currentProductName);
            }
        }
        else {
            hideSellingScreen();
            cash = business.getCash();
            endOfWeek = business.endToday();
            dayStarted = false;
            if (endOfWeek) {
                // show the statistics
                weekendStatistics();
            }
            else {
                appNameLabel.setText("DAY " + business.getDay() + " OF THE WEEK");
                appNameLabel.setVisible(true);
                startButton.setVisible(true);
                menuImage.setVisible(false);
            }
        }
    }

    private void weekendStatistics() {
        String stat = "\n";
        HashMap<Integer, Double> weeklySales = business.getWeeklySales();
        for (Integer day : weeklySales.keySet()){
            stat += "Day " + day + ": $" + weeklySales.get(day) + "\n";
        }
        statisticsLabel.setText(stat);
        okButton.setVisible(true);
        statisticsLabel.setVisible(true);
    }

    private void finishPreparation() {
        makeButton.setVisible(false);
        stockUpButton.setVisible(false);
        finishButton.setVisible(false);
        cashLabel.setVisible(false);
        inventoryLabel.setVisible(false);
        menuLabel.setVisible(false);
        priceUpButton.setVisible(false);
        priceDownButton.setVisible(false);
        predictionLabel.setVisible(false);

        appNameLabel.setText("DAY " + business.getDay() + " OF THE WEEK");
        appNameLabel.setVisible(true);
        startButton.setVisible(true);
    }

    public void modifyMenu() {
        // call adjust price
        // call add new product
    }

    public void weekendManagement() {
        appNameLabel.setVisible(false);
        startButton.setVisible(false);
        okButton.setVisible(false);
        statisticsLabel.setVisible(false);

        makeButton.setVisible(true);
        stockUpButton.setVisible(true);
        finishButton.setVisible(true);
        cashLabel.setVisible(true);
        inventoryLabel.setVisible(true);
        menuLabel.setVisible(true);
        updateMenu();
        priceUpButton.setVisible(true);
        priceDownButton.setVisible(true);
        predictionLabel.setVisible(true);
        updatePrediction();
    }
}
