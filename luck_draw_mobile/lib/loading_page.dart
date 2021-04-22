import 'package:flutter/material.dart';
import 'package:flutter_spinkit/flutter_spinkit.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'constants.dart';

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
          builder: (context)=>Container()
      ));
    }
    else{
      Navigator.pushReplacement(context, MaterialPageRoute(
        builder: (context)=>Container()
      ));
    }
  }
  
  @override
  void initState(){
    super.initState();
    // _checkLogin();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: SpinKitRotatingCircle(
          color: kBrightBlue,
          size: 50.0,
        ),
      ),
    );
  }
}
