import 'package:flutter/material.dart';
import '../background/login_template.dart';
import '../components/custom_button.dart';

class InitialPage extends StatelessWidget {
  InitialPage();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Stack(
        children: [
          Container(
            height: double.infinity,
            width: double.infinity,
            child: CustomPaint(painter: LoginTemplate()),
          ),
          Container(
            width: double.infinity,
            child: Column(
              children: [
                SizedBox(height: 50,),
                Image(
                  height: 45,
                  image: AssetImage('images/name_logo.png'),
                ),
                SizedBox(
                  height: 90,
                ),
                Image(
                  height: 150,
                  image: AssetImage('images/bar_image.png'),
                ),
                SizedBox(
                  height: 150,
                ),
                CustomButton(text: 'Register', onPress: (){}),
                SizedBox(
                  height: 20,
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
