import 'package:flutter/material.dart';

class CustomDialog extends StatelessWidget {
  final TextEditingController textEditingController;
  final String title;
  final String optionRightText;
  final Function optionRightOnPressed;
  final String optionLeftText;
  final Function optionLeftOnPressed;

  CustomDialog({
      this.title,
      this.textEditingController,
      this.optionRightText,
      this.optionRightOnPressed,
      this.optionLeftText,
      this.optionLeftOnPressed});

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: Column(
        children: [
          Text(
            title,
            style: TextStyle(fontSize: 15),
          ),
          TextField(
            controller: this.textEditingController,
          )
        ],
      ),
      actions: <Widget>[
        TextButton(
          child: Text(
            optionLeftText,
            style: TextStyle(color: Theme.of(context).accentColor),
          ),
          onPressed: optionLeftOnPressed,
        ),
        TextButton(
          child: Text(
            optionRightText,
            style: TextStyle(color: Theme.of(context).accentColor),
          ),
          onPressed: optionRightOnPressed,
        ),
      ],
    );
  }
}
