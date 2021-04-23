import 'package:flutter/material.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:luck_draw_mobile/background/primary_template.dart';
import 'package:luck_draw_mobile/models/event.dart';
import 'package:luck_draw_mobile/models/raffle-ticket.dart';
import 'package:luck_draw_mobile/models/user.dart';
import 'package:luck_draw_mobile/models/winner.dart';
import 'package:luck_draw_mobile/services/constants.dart';
import 'package:luck_draw_mobile/services/luck_draw_api_service.dart';

class HomePage extends StatefulWidget {
  final User user;

  HomePage({this.user});

  @override
  _HomePageState createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  int _selectedIndex = 0;
  List<Event> events = [];
  List<Winner> winners = [];
  RaffleTicket raffleTicket;
  bool isFetchingEvents = true;
  bool isFetchingWinners = true;

  void _onItemTapped(int index) {
    setState(() {
      _selectedIndex = index;
    });
  }

  _generateRaffleTicket() async{
    raffleTicket  = await LuckyDrawApiService.getRaffleTicket(widget.user.uid);
    Fluttertoast.showToast(msg: "Raffle Ticket Generated with ID: ${raffleTicket.rid}");
  }

  _participate(String eid) async {
    if(raffleTicket==null){
      Fluttertoast.showToast(msg: "Get raffle ticket to participate in event");
      return;
    }
    print('HERE');
    await LuckyDrawApiService.participateInEvent(raffleTicket);
    Fluttertoast.showToast(msg: "Participation Successful! All the best.");
  }

  _fetchEvents() async {
    events = await LuckyDrawApiService.getEvents();
    isFetchingEvents = false;
    print("events " + events.length.toString());
    events.sort((a, b) => a.date.compareTo(b.date));
    setState(() {});
  }

  _fetchWinners() async {
    winners = await LuckyDrawApiService.getWinners();
    isFetchingWinners = false;
    print("winners " + winners.length.toString());
    winners.sort((a, b) => a.date.compareTo(b.date));
    setState(() {});
  }

  @override
  void initState() {
    super.initState();
    _fetchEvents();
    _fetchWinners();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      floatingActionButton: FloatingActionButton(
        backgroundColor: kReddishPink,
        child: Icon(
          Icons.local_movies_outlined,
          color: Colors.white,
        ),
        onPressed: () {
          _generateRaffleTicket();
        },
      ),
      body: Stack(
        children: [
          Container(
            height: double.infinity,
            width: double.infinity,
            child: CustomPaint(painter: PrimaryTemplate()),
          ),
          Container(
            width: double.infinity,
            child: Column(
              children: [
                SizedBox(
                  height: 50,
                ),
                Image(
                  height: 45,
                  image: AssetImage('images/name_logo.png'),
                ),
                SizedBox(
                  height: 10,
                ),
                Text(
                  'Welcome, ${widget.user.name}',
                  style: TextStyle(
                      color: Colors.white,
                      fontSize: 16,
                      fontWeight: FontWeight.bold,
                      letterSpacing: 4),
                )
              ],
            ),
          ),
          Container(
            padding: EdgeInsets.fromLTRB(50, 180, 50, 10),
            width: double.infinity,
            child: _selectedIndex == 0 ? eventsList() : winnersList(),
          ),
        ],
      ),
      bottomNavigationBar: BottomNavigationBar(
        backgroundColor: kPeach,
        items: const <BottomNavigationBarItem>[
          BottomNavigationBarItem(
            icon: Icon(Icons.event),
            label: 'Events',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.wine_bar),
            label: 'Winners',
          ),
        ],
        currentIndex: _selectedIndex,
        selectedItemColor: kReddishPink,
        onTap: _onItemTapped,
      ),
    );
  }

  Widget eventsList() {
    return isFetchingEvents
        ? Center(
            child: CircularProgressIndicator(
              valueColor: AlwaysStoppedAnimation<Color>(kReddishPink),
            ),
          )
        : ListView.builder(
            itemCount: events.length,
            itemBuilder: (context, index) => Padding(
              padding: const EdgeInsets.all(8.0),
              child: Container(
                height: 120,
                decoration: BoxDecoration(
                    color: Colors.redAccent[100],
                    borderRadius: BorderRadius.all(Radius.circular(5))),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(events[index].date,style: TextStyle(color: Colors.white,fontWeight: FontWeight.bold),),
                    Text(events[index].time,style: TextStyle(color: Colors.white,fontWeight: FontWeight.bold),),
                    Text(events[index].prize,style: TextStyle(color: Colors.white,fontWeight: FontWeight.bold,fontSize: 16),),
                    Container(
                      width: double.infinity,
                      child: RaisedButton(
                        color: kReddishPink,
                        onPressed: () {
                          _participate(events[index].eid);
                        },
                        child: Text(
                          'Participate',
                          style: TextStyle(color: Colors.white,fontWeight: FontWeight.bold),
                        ),
                      ),
                    )
                  ],
                ),
              ),
            ),
          );
  }

  Widget winnersList() {
    return isFetchingWinners
        ? Center(
            child: CircularProgressIndicator(
              valueColor: AlwaysStoppedAnimation<Color>(kReddishPink),
            ),
          )
        : ListView.builder(
            itemCount: winners.length,
            itemBuilder: (context, index) => Padding(
              padding: const EdgeInsets.all(8.0),
              child: Container(
                child: Padding(
                  padding:
                      const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Text(
                        winners[index].date,
                        style: TextStyle(
                            color: Colors.white, fontWeight: FontWeight.bold),
                      ),
                      Text(
                        winners[index].name,
                        style: TextStyle(
                            color: Colors.white, fontWeight: FontWeight.bold),
                      )
                    ],
                  ),
                ),
                height: 60,
                decoration: BoxDecoration(
                    color: Colors.green[400],
                    borderRadius: BorderRadius.all(Radius.circular(30))),
              ),
            ),
          );
  }
}
