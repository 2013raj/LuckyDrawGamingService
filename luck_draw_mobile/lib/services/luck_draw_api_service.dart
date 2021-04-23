import 'dart:convert';

import 'package:http/http.dart' as http;
import 'package:luck_draw_mobile/models/event.dart';
import 'package:luck_draw_mobile/models/raffle-ticket.dart';
import 'package:luck_draw_mobile/models/user.dart';
import 'package:luck_draw_mobile/models/winner.dart';

class LuckyDrawApiService {
  static const String API_ENDPOINT =
      "https://lucky-draw-gaming-service.herokuapp.com";

  static Future<User> registerUser(String name) async {
    var response = await http.post(Uri.parse(API_ENDPOINT + "/register"),
        headers: <String, String>{
          'Content-Type': 'application/json; charset=UTF-8',
        },
        body: jsonEncode(<String, String>{
          'name': name,
        }));

    var data = await jsonDecode(response.body);
    print(data["text"]);
    return User(uid: data["uid"], name: name);
  }

  static Future<RaffleTicket> getRaffleTicket(String uid) async {
    var response = await http.get(
        Uri.parse(API_ENDPOINT + "/raffle-ticket/$uid"),
        headers: <String, String>{
          'Content-Type': 'application/json; charset=UTF-8',
        });

    var data = await jsonDecode(response.body);
    print(data["text"]);
    return RaffleTicket(rid: data["rid"], uid: uid);
  }

  static Future<void> participateInEvent(RaffleTicket raffleTicket) async {
    var response = await http.post(Uri.parse(API_ENDPOINT + "/participate"),
        headers: <String, String>{
          'Content-Type': 'application/json; charset=UTF-8',
        },
        body: jsonEncode(<String, String>{
          'rid': raffleTicket.rid,
          'eid': raffleTicket.eid
        }));

    var data = await jsonDecode(response.body);
    print(data["text"]);
  }

  static Future<List<Winner>> getWinners() async {
    var response = await http
        .get(Uri.parse(API_ENDPOINT + "/winners"), headers: <String, String>{
      'Content-Type': 'application/json; charset=UTF-8',
    });
    var data = await jsonDecode(response.body);
    List<Winner> winners = [];
    Map<String,String> eventsMap = Map<String, String>.from(data);
    eventsMap.forEach((key, value) {
      winners.add(Winner(date: key,name: value));
    });
    return winners;
  }

  static Future<List<Event>> getEvents() async {
    var response = await http
        .get(Uri.parse(API_ENDPOINT + "/events"), headers: <String, String>{
      'Content-Type': 'application/json; charset=UTF-8',
    });

    List<Event> events = [];
    var data = await jsonDecode(response.body);
    Map<String,dynamic> eventsMap = Map<String, dynamic>.from(data);
    eventsMap.keys.forEach((event) {
      events.add(Event(
          eid: data[event]["eid"],
          date: data[event]["date"],
          time: data[event]["time"],
          prize: data[event]["prize"],
          winner: data[event]["winner"]));
    });
    return events;
  }
}
