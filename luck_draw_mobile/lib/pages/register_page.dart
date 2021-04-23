import 'package:flutter/material.dart';
import 'package:luck_draw_mobile/components/custom_dialog.dart';
import 'package:luck_draw_mobile/models/user.dart';
import 'package:luck_draw_mobile/pages/home_page.dart';
import 'package:luck_draw_mobile/services/constants.dart';
import 'package:luck_draw_mobile/services/luck_draw_api_service.dart';
import 'package:modal_progress_hud/modal_progress_hud.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../background/login_template.dart';
import '../components/custom_button.dart';

class InitialPage extends StatefulWidget {
  @override
  _InitialPageState createState() => _InitialPageState();
}

class _InitialPageState extends State<InitialPage> {
  bool isLoading = false;
  TextEditingController nameEditingController = TextEditingController();

  _registerUser() async {
    setState(() {
      isLoading = true;
    });

    User user = await LuckyDrawApiService.registerUser(nameEditingController.text);

    SharedPreferences prefs = await SharedPreferences.getInstance();
    prefs.setString("uid", user.uid);
    prefs.setString("name", user.name);

    Navigator.push(context, MaterialPageRoute(builder: (context) => HomePage(user: user,)));

    setState(() {
      isLoading = false;
    });

  }

  @override
  Widget build(BuildContext context) {
    return ModalProgressHUD(
      inAsyncCall: isLoading,
      color: kReddishPink,
      child: Scaffold(
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
                  SizedBox(
                    height: 50,
                  ),
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
                  CustomButton(
                      text: 'Register',
                      onPress: () async {
                        showDialog(
                            context: context,
                            builder: (context) => CustomDialog(
                                  title: "Enter Name",
                                  textEditingController: nameEditingController,
                                  optionLeftText: "Cancel",
                                  optionLeftOnPressed: () {
                                    Navigator.pop(context);
                                  },
                                  optionRightText: "Register",
                                  optionRightOnPressed: () {
                                    _registerUser();
                                    Navigator.pop(context);
                                  },
                                ));
                      }),
                  SizedBox(
                    height: 20,
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
