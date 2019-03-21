# Title

##1. Subtitle 1

Text.

1. Read input file, parse each row as an Order
2. Calculate required time for each hour from 6am to 10pm.
3. Calculate 


##2. Task given

The task is not fully clear, for example, the questions are:

- First of all, do we have the full list of orders at 6am or list is dynamically updating? Can we plan delivery exact to the order time or we always have the latency? It affects the strategy of delivery planning.
- The positive delivery window is 2 hours - are those hours *after* timestamp or *around*?
- Is there any latency when drone has delivered (drop parcel) and returned to base (take new parcel)?

Let's say we have two cases (they seem to me not a spherical cow):

##### [Live list](#live-list)

- Orders are coming during the day, so drone starts not earlier than the timestamp, and we have minimal latency due to distance.
- When drone is returning to base there can be:
  - no available orders, drone waits and checks the order with interval of 15 minutes,
  - 1 available order, drone starts delivery,
  - 2+ available orders, drone starts delivery in order given.
- Order of 2+ available orders is sorting the following way:
  - less distance has higher priority,
  - potential promoters have higher priority, neutral feedback has less one,
  - if order already expired (delayed more than 4 hours) then we can postpone it more because it is already has negative feedback.

##### [Predefined list](#predefined-list)

- Orders are all in the list, so drone can start earlier than the order timestamp to deliver the order at a time.
- When drone returns to base it takes the next order or wait for time allowed to next delivery.
- The simplest strategy here - it takes N (maximum is restricted by `IN_ADVANCE_MAX`) closest not completed potentially positive orders and iterates over all possible queues to find the one with maximum NPS.
- It takes (shifts) iteratively the first order in queue while queue size is longer than `IN_ADVANCE_RECALCULATE`, then it refreshes the queue.
- The optimal queue is selecting based on coefficient similar to NPS over orders checked. This coefficient is updated with fees:
    - decrease fee if order completed in neutral window,
    - decrease fee twice if order completed in positive window,
    - increase fee if order completed out of positive window,
    - increase fee twice if order completed of neutral window,
    - increase fee using distance,
    - some other fee can be added.
     
Of course, we can implement other strategies, even using Machine Learning in final case. By the way, ML can be applied to find the optimal values for all entities listed above.  




Text 3 `code 1`:

    code 2
    code 3

Text 4