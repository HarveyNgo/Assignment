# Assignment

The progress below
1. Read json file into String

2. run the jsonArray and create Portfolio class: 
    - this class have ID, 3 ArrayList: NavsItemList, MonthlyNavsList (init 12 element),QuarterlyNavsList (init 4 element)
    
3. run the Navs jsonArray and use Gson to convert to NavsItem class (NavsItem have Date and amount)

4. Add each NavsItem into Portfolio.NavsItemList()

5. While converting NavsItem class, get MonthData and QuarterData:
    - compare 2 object Navs to check they are the same Month 
      the greater Date will be insert into MonthlyNavsList with index = Month_Of_Year
        -   example: compare 2017/01/01 and 2017/01/02, they are the same month, insert 2017/01/02 into MonthlyNavsList with index =0
               continue to the end of month, the next month (Feb) will be inserted into MonthlyNavsList with index =1
      
    - compare 2 object Navs to check they are the same Month and is a quarter month (Mar, Jun, Sep,Dec)
      the greater Date will be insert into QuarterlyNavsList with index = Month_Of_Year
        -   example: compare 2017/03/01 and 2017/03/02, they are the same month and a quarter month
              , insert 2017/01/02 into MonthlyNavsList with index =0
              continue to the end of month, the next quarter month (Jun) will be inserted into QuarterlyNavsList with index =1
              
 6. After finish Converting for Json string, we will have a Portfio with ID and 3 ArrayList
 
 7. Using lib MPAndroidChart to display data into Line Chart base on each ArrayList for 3 option: Daily/Monthly/Quarter
 
 8.I also make some unit test, but no much

 9.I can not make Firebase saving data on time


 
