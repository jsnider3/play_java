import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

/*
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.service import Service as ChromeService
import time
from webdriver_manager.chrome import ChromeDriverManager
import undetected_chromedriver as uc*/

/* TODOs
- **get_one_day_food:**
    - Extract headers and create the initial dictionary structure into a separate function.
    - Create a function for processing a single row of food data.
- **get_one_day_exercise:**
    Similar to `get_one_day_food`, split extracting headers and processing individual rows into separate functions.
- **monthly_macro_printout:**
    - Separate the macro calculation logic for a single day into a function for better readability.
'''*/

/*class DiaryDay(object):

    def __init__(self, dictform):
        self.backup = dictform
        self.date = datetime.strptime(dictform['Date'], '%Y-%m-%d')
        self.meals = dictform['Meals']
        self.summary = dictform['Summary']

    def in_month(self, month):
        return self.date.month == month

public static void enter_password(){
    // Bypass the MFP password screen.
    // Open MyFitnessPal diary
    driver.get('https://www.myfitnesspal.com/food/diary/sniderjosh1')

    // Input password.
    WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.ID, 'password'))
    )

    password_input = driver.find_element(By.ID, 'password')

    password_input.send_keys('selenium')
    time.sleep(2)

    password_input.send_keys(Keys.RETURN)
    time.sleep(2)
}

public static void get_food_list(){//all_diary):
    ''' Return a list of all the food you've eaten. '''
    foods = set([])
    for day in all_diary:
        meals = day['Food']['Meals']
        daily_bread = meals['Breakfast'] + meals['Lunch'] + meals['Dinner'] + meals['Snacks']
        for food_name in (food['Food'] for food in daily_bread):
            food_name = ','.join(food_name.rsplit(',', maxsplit=1)[:-1])
            foods.add(food_name)
    return foods
}

public static void get_one_day_food(){//driver, day):
    // Get the info for a single day of MFP.
    driver.get('https://www.myfitnesspal.com/food/diary/sniderjosh1?date={0}'.format(day))

    // Wait for the diary to load
    WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.ID, 'diary-table'))
    )

    // Extract entries
    entries = []
    // Find the table within the <tbody> tags
    tbody = driver.find_element(By.CSS_SELECTOR, 'table tbody')
    rows = tbody.find_elements(By.TAG_NAME, 'tr')

    table_data = []

    // Loop through each row
    for row in rows:
        columns = row.find_elements(By.TAG_NAME, 'td')
        table_data.append([column.text for column in columns])
    // Convert the data to a dictionary.
    diary = {'Meals' : {'Breakfast': []}, 'Summary': {}, 'Date': day}
    headers = ['Food'] + table_data[0][1:]
    current_meal = 'Breakfast'

    for row in table_data[1:]:
        if row[0] in [' ', 'Quick Tools']:
            pass
        elif len(row) == 1:
            current_meal = row[0]  # e.g., 'Breakfast'
            diary['Meals'][current_meal] = []
        elif row[0] in ["Totals", "sniderjosh1 Daily Goal", "Remaining"]:
            diary['Summary'][row[0]] = {table_data[0][1:][i]: cell for i, cell in enumerate(row[1:-1])}
        elif len(row) > 2:
            if current_meal:
                entry = {headers[i]: cell for i, cell in enumerate(row[:-1])}
                diary['Meals'][current_meal].append(entry)
    // DEBUG Get food notes. Take the "note" class and get its inner text.
    food_note = driver.find_element(By.CLASS_NAME,'note')
    diary['Note'] = food_note.get_attribute("innerText")
    return diary
}

def get_one_day_exercise(driver, day):
    ''' Get the info for a single day of MFP.
        TODO Get exercise notes. '''
    driver.get('https://www.myfitnesspal.com/exercise/diary/sniderjosh1?date={0}'.format(day))

    # Wait for the diary to load
    WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.ID, 'cardio-diary'))
    )

    # Extract entries
    entries = []
    # Find the table within the <tbody> tags
    tbody = driver.find_element(By.CSS_SELECTOR, 'table tbody')
    rows = tbody.find_elements(By.TAG_NAME, 'tr')

    cardio_data = []

    # Loop through each row
    for row in rows:
        columns = row.find_elements(By.TAG_NAME, 'td')
        cardio_data.append([column.text for column in columns])
    # Convert the data to a dictionary.
    diary = {'Cardio' : [], 'Strength': [], 'Date': day, 'Summary': {}}
    cardio_head = ['Exercise', 'Minutes', 'Calories Burned']
    for row in cardio_data:
        if len(row) == 3:
            diary['Cardio'].append({cardio_head[i]: cell for i, cell in enumerate(row)})
    # TODO Add summary data.
    tbody = driver.find_element(By.CSS_SELECTOR, 'table tfoot')
    rows = tbody.find_elements(By.TAG_NAME, 'tr')
    footer_data = []
    for row in rows:
        columns = row.find_elements(By.TAG_NAME, 'td')
        footer_data.append([column.text for column in columns])
    # Convert the data to a dictionary.
    for row in footer_data:
        if len(row) > 2:
            diary['Summary'][row[0]] = {'Minutes':row[1], 'Calories Burned':row[2]}
    #
    tbody = driver.find_element(By.XPATH, '//*[@id="main"]/div[2]/table[2]/tbody')

    rows = tbody.find_elements(By.TAG_NAME, 'tr')

    strength_data = []

    # Loop through each row
    for row in rows:
        columns = row.find_elements(By.TAG_NAME, 'td')
        strength_data.append([column.text for column in columns])
    strength_head = ['Exercise', 'Sets', 'Reps/Set', 'Weight/Set']
    for row in strength_data:
        if len(row) == 4:
            diary['Strength'].append({strength_head[i]: cell for i, cell in enumerate(row)})
    # DEBUG Get food notes. Take the "note" class and get its inner text.
    food_note = driver.find_element(By.CLASS_NAME,'note')
    diary['Note'] = food_note.get_attribute("innerText")
    return diary

def get_one_day_both(driver, day):
    combo = dict()
    combo['Food'] = get_one_day_food(driver, day)
    combo['Exercise'] = get_one_day_exercise(driver, day)
    return combo

def login():
    driver.get('https://www.myfitnesspal.com/account/login')

    # TODO Accept cookies
    try:
        accept_button = WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.XPATH, '//button[text()="ACCEPT"]'))
        )
        accept_button.click()
    except:
        print("ACCEPT button not found or not clicked.")
    # Enter credentials and submit
    USERNAME = 'sniderjosh1'
    PASSWORD = 'LOL.LMAO'
    username_input = driver.find_element(By.ID, 'email')
    password_input = driver.find_element(By.ID, 'password')

    username_input.send_keys(USERNAME)
    time.sleep(2)
    password_input.send_keys(PASSWORD)
    time.sleep(2)

    password_input.send_keys(Keys.RETURN)

    time.sleep(6)
    # TODO Recaptcha?


*/

// Helper class to store monthly nutritional totals

public class MFPDiary {

  static class NutritionTotals {
    int protein = 0;
    int fat = 0;
    int calories = 0;
    int carbs = 0;

    void updateTotals(int protein, int fat, int calories, int carbs) {
      this.protein += protein;
      this.fat += fat;
      this.calories += calories;
      this.carbs += carbs;
    }
  }

  public static WebDriver setup() {
    /*" Initialize the Chrome driver and handle any initial password entry. "
    # Initialize Chrome driver
    options = webdriver.ChromeOptions()
    #options.add_argument('--headless')  # Run Chrome in headless mode (no GUI). Remove this line if you want to see the browser in action.
    driver = uc.Chrome(options=options)

    enter_password(driver)
    return driver*/
    // Replace with your ChromeDriver path
    System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");

    WebDriver driver = new ChromeDriver();

    // Replace with the actual website URL
    driver.get("https://www.myfitnesspal.com/food/diary/sniderjosh1");

    // Replace "textBoxId" with the actual ID of the text box
    WebElement textBox = driver.findElement(By.id("password"));
    textBox.click(); // Click on the text box

    // Replace "textToType" with the text you want to enter
    textBox.sendKeys("selenium");

    textBox.sendKeys(Keys.ENTER); // Press Enter

    return driver;
  }

  public static void monthly_macro_printout(JsonArray diary) {
    // Print out the monthly macro breakdowns.
    System.out.println("month, calories, carbs(g), fat(g), protein(g)");
    Map<YearMonth, NutritionTotals> monthlyTotals = new HashMap<>();

    // Iterate through the diary entries
    for (JsonElement dayElement : diary) {
      JsonObject day = dayElement.getAsJsonObject();
      JsonObject foodData = day.getAsJsonObject("Food");
      JsonObject summaryData = foodData.getAsJsonObject("Summary");
      JsonObject totals = summaryData.getAsJsonObject("Totals");

      // Extract date and nutritional values
      String dateString = foodData.get("Date").getAsString();
      System.out.println(dateString);
      if (totals != null) {
        // Extract nutritional values with comma removal
        String proteinString = totals.get("Protein\ng").getAsString().replace(",", "");
        int protein = Integer.parseInt(proteinString);

        // Similarly for fat, calories, and carbs
        String fatString = totals.get("Fat\ng").getAsString().replace(",", "");
        int fat = Integer.parseInt(fatString);

        String caloriesString = totals.get("Calories\nkcal").getAsString().replace(",", "");
        int calories = Integer.parseInt(caloriesString);

        String carbsString = totals.get("Carbs\ng").getAsString().replace(",", "");
        int carbs = Integer.parseInt(carbsString);

        // Calculate month from date
        LocalDate date = LocalDate.parse(dateString);
        YearMonth yearMonth = YearMonth.from(date);

        // Update monthly totals
        NutritionTotals monthTotals = monthlyTotals.getOrDefault(yearMonth, new NutritionTotals());
        monthTotals.updateTotals(protein, fat, calories, carbs);
        monthlyTotals.put(yearMonth, monthTotals);
      }
    }
    System.out.println(monthlyTotals.size());
    // Print monthly totals
    for (Map.Entry<YearMonth, NutritionTotals> entry : monthlyTotals.entrySet()) {
      YearMonth yearMonth = entry.getKey();
      NutritionTotals totals = entry.getValue();
      System.out.println(yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")) + ":");
      System.out.println(
          "  Calories: "
              + totals.calories
              + "  Carbohydrates: "
              + totals.carbs
              + "  Protein: "
              + totals.protein
              + "  Fat: "
              + totals.fat);
    }
  }

  public static void popular_food_list(JsonArray diary) {
    /* Count the occurrences of each food in the diary and print a sorted list of popular foods. */
    Map<String, Integer> foodCounts = new HashMap<>();

    for (int i = 0; i < diary.size(); i++) {
      JsonObject day = diary.get(i).getAsJsonObject();

      // Access meals for the day
      JsonObject meals = day.getAsJsonObject("Food").getAsJsonObject("Meals");

      List<String> foodItems = new ArrayList<>();
      for (JsonElement item : meals.getAsJsonArray("Breakfast")) {
        foodItems.add(item.getAsJsonObject().get("Food").getAsString());
      }
      for (JsonElement item : meals.getAsJsonArray("Lunch")) {
        foodItems.add(item.getAsJsonObject().get("Food").getAsString());
      }
      for (JsonElement item : meals.getAsJsonArray("Dinner")) {
        foodItems.add(item.getAsJsonObject().get("Food").getAsString());
      }
      for (JsonElement item : meals.getAsJsonArray("Snacks")) {
        foodItems.add(item.getAsJsonObject().get("Food").getAsString());
      }

      // Count the occurrences of each food item
      for (String foodItem : foodItems) {
        foodItem = foodItem.trim(); // Remove leading/trailing whitespace
        if (!foodItem.isEmpty()) {
          foodCounts.put(foodItem, foodCounts.getOrDefault(foodItem, 0) + 1);
        }
      }
    }

    // Sort the food counts in descending order of frequency
    TreeMap<String, Integer> sortedFoodCounts =
        new TreeMap<>((a, b) -> foodCounts.get(b) - foodCounts.get(a));
    sortedFoodCounts.putAll(foodCounts);

    // Print the sorted list of foods and counts
    for (Map.Entry<String, Integer> entry : sortedFoodCounts.entrySet()) {
      System.out.println(entry.getKey() + ": " + entry.getValue());
    }
  }

  public static void main(String[] args) {
    // Replace this with the actual path to your JSON file
    String jsonFilePath = "/mnt/c/users/josh/Documents/Git/MFP_Tools/mfp_diary_both.json";
    JsonArray diary = null;
    try (FileReader reader = new FileReader(jsonFilePath)) {
      // Create a Gson object
      Gson gson = new Gson();

      // Parse the JSON file into a JsonObject
      diary = gson.fromJson(reader, JsonArray.class);

    } catch (IOException e) {
      e.printStackTrace();
    }
    if (diary != null) {
      // Now you have the parsed JSON data in the 'json' variable
      // Initialize variables to keep track of the latest date
      String latestDate = "";

      // Iterate through each JSON object in the array
      for (JsonElement jsonElement : diary) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        for (String key : jsonObject.keySet()) {
          // Get the "Food" or "Exercise" object
          JsonObject categoryObject = jsonObject.getAsJsonObject(key);

          // If the "Date" property exists within this object, proceed
          if (categoryObject.has("Date")) {
            String currentDate = categoryObject.get("Date").getAsString();

            // Compare with the current latest date and update if necessary
            if (latestDate.isEmpty() || currentDate.compareTo(latestDate) > 0) {
              latestDate = currentDate;
            }
          }
        }
      }
      // Print the latest date
      System.out.println("Latest date: " + latestDate);
      boolean update = false;
      // String startDateStr = "2023-12-25";  // Replace with your starting date

      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      LocalDate startDate = LocalDate.parse(latestDate, formatter);
      LocalDate today = LocalDate.now();

      // Iterate through the dates, starting from the day after the start date
      // and stopping before today
      for (LocalDate date = startDate.plusDays(1); date.isBefore(today); date = date.plusDays(1)) {
        System.out.println(date.format(formatter));
        // diary.append(get_one_day_both(driver, current_date.strftime('%Y-%m-%d')))
        update = true;
      }
      if (update) {
        System.out.println("Updated...");
        // json.dump(all_diary, file)
        try (FileWriter writer = new FileWriter("test_output.json")) {
          Gson gson = new Gson();
          gson.toJson(diary, writer);
          System.out.println("JsonArray written to file: " + "test_output.json");

        } catch (IOException e) {
          System.err.println("Error writing to file: " + e.getMessage());
        }
      }
      // popular_food_list(diary);
      monthly_macro_printout(diary);
      setup();
    }
  }
}
