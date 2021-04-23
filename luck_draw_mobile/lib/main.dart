import 'package:flutter/material.dart';

import 'services/constants.dart';
import 'pages/loading_page.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Lucky Draw Mobile Game',
      theme: ThemeData.light().copyWith(
        primaryColor: kPinkish,
        scaffoldBackgroundColor: kPeach,
        textTheme: ThemeData.light().textTheme.apply(
          fontFamily: 'NunitoSans',
        ),
        primaryTextTheme: ThemeData.light().textTheme.apply(
          fontFamily: 'NunitoSans',
        ),
        accentTextTheme: ThemeData.light().textTheme.apply(
          fontFamily: 'NunitoSans',
        ),
      ),
      home: LoadingPage(),
    );
  }
}
