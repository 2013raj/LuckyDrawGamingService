import 'package:flutter/material.dart';
import 'package:flutter_spinkit/flutter_spinkit.dart';
import 'package:luck_draw_mobile/models/user.dart';
import 'package:luck_draw_mobile/pages/home_page.dart';
import 'package:luck_draw_mobile/pages/register_page.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../services/constants.dart';

class LoadingPage extends StatefulWidget {
  @override
  _LoadingPageState createState() => _LoadingPageState();
}

class _LoadingPageState extends State<LoadingPage> {

  _checkLogin() async{
    SharedPreferences prefs = await SharedPreferences.getInstance();
    bool loggedIn = prefs.containsKey('uid');
    if(loggedIn){
      Navigator.pushReplacement(context, MaterialPageRoute(
          builder: (context)=>HomePage(user: User(uid: prefs.get("uid",),name: prefs.get("name")))
      ));
    }
    else{
      Navigator.pushReplacement(context, MaterialPageRoute(
        builder: (context)=> InitialPage()
      ));
    }
  }
  
  @override
  void initState(){
    super.initState();
    _checkLogin();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: SpinKitRotatingCircle(
          color: kReddishPink,
          size: 50.0,
        ),
      ),
    );
  }
}
