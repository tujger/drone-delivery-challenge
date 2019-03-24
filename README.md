# Drone delivery challenge

## Synopsis

### The main plan

- Read input file, parse each row as an Order.
- Start at 6am and request list of available orders, fulfill the first order, mark it as completed.
- Repeat previous action continuously until 6pm.
- If list of orders is empty then wait 1 minute and try again.

Thus, the strategy of planning is responsibility of method which builds the queue of available orders.

### Given task

The task given was not fully clear, for example, the questions happened:

- First, do we already have the full list of orders at 6am or it is dynamically updating? Can we plan delivery exact to the order time or we always have the latency? It affects the strategy of delivery planning.
- The positive delivery window is 2 hours - are those hours *after* timestamp or *around*?
- Is there any latency when drone has delivered (thrown parcel) and returned to base (taken new parcel)?

Let's say we have two cases (they seem to me not a spherical cow in vacuum):

##### Live list

- Orders are coming during the day, so drone starts not earlier than the order timestamp, and we definitely have a minimal latency due to distance.
- When drone is returning to base there can be following:
  - no available orders, drone waits and checks the queue every minute,
  - 1 available order, drone starts delivery,
  - 2+ available orders, drone starts delivery by calculated queue.
- Queue of 2+ available orders is sorting the following way:
  - less distance has higher priority,
  - potential promoters have higher priority, neutral feedback has less one,
  - if order already expired (delayed more than 4 hours) then we can postpone it more because it is already has negative feedback.

##### Predefined list

- Orders are all in the list already, so drone can start earlier than the order timestamp to deliver the order on time.
- When drone returns to base it takes the next order or waits for time calculated to next delivery.
- The simplest strategy here - it takes N (maximum is restricted by `IN_ADVANCE_MAX`) of closest not completed potentially positive orders and iterates over all possible permutations to find the queue with maximum NPS. _(Of course, it is extremely nonoptimal way and can be improved.)_
- It pulls (shifts) iteratively the first item from the queue until queue is shorter than `IN_ADVANCE_RECALCULATE`, then it refreshes the queue.
- The optimal queue is selecting based on coefficient similar to NPS over orders checked. This coefficient is updated with fees:
    - make better if order completed within 4 hours,
    - make better twice if order completed within 2 hours,
    - make worse twice if order completed after 4 hours,
    - make worse based on delay,
    - tune using distance,
    - some other fee can be added.
   
To get the best result it's need to build a grid of possible values of all entities listed above and test all cases to find the optimal values.  

## How to use

Make a clone of the project into your project:

    git clone https://github.com/tujger/drone-delivery-challenge.git

Run:

    ./gradlew build
    
or (Windows):
    
    ./gradlew.bat build
    
Then look at the output folder and run:

    java -cp ./out/production/classes dev.tujger.ddc.Main ./src/main/resources/input.txt

Fix paths if it is necessary.

    
