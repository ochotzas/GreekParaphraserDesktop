## What is Greek Paraphrase(r)

Greek Paraphrase(r) is a project that aims to provide a simple, easy-to-use, and free way to paraphrase Greek texts.

## What you can do with Greek Paraphrase(r)

- Paraphrase a given text.
- Correct a text's spelling mistakes (if any) and grammar mistakes (if any).
- Save a text for later use (Supported file formats: `.ptf` - Check below for further information).
- Open a saved text (Supported file formats: `.ptf` - Check below for further information).

## File format support

`.ptf`: Paraphrase Text File

The `.ptf` file is a simple text file that contains the text to paraphrase and its corresponding paraphrase in the following format (but encoded in Base64):

```
## Αρχικό κείμενο:
<text_to_paraphrase>
## Παραφρασμένο κείμενο:
<paraphrased_text>
```

## How to set up Greek Paraphrase(r)

Just clone the repository and edit the .env files to set your API keys (e.g. you might get your own key for the apps which you want to use [here](https://rapidapi.com/hub)).

.env files must be written in UTF-8 encoding, without enter (\n) and must be inline with the following format:

```
<your_key><EOF>
```


## Screenshots

### Main Screen
<img src="https://raw.githubusercontent.com/ochotzas/GreekParaphraserDesktop/main/screenshots/main_screen_on_action.png" width="600">

### File load
<img src="https://raw.githubusercontent.com/ochotzas/GreekParaphraserDesktop/main/screenshots/file_load.png" width="600">

## Contributing

If you have any questions or comments, please open an issue on [Issues](https://github.com/ochotzas/GreekParaphraserDesktop/issues).

## Changelog
- v0.0.4-alpha:
  * Modify errors print values.
- v0.0.3-alpha:
  * Add the action icon that is selected on the left-down corner of the main window.
- v0.0.2-alpha:
  * Show a related message regarding the files that contain the API keys.
  * Create a drop-down action menu and adjust some components.
- v0.0.1-alpha:
  * Initial release.

## License

Copyright © 2022, Olger Chotza. All rights and lefts reserved.

This project is licensed under the general terms of the MIT license.

## Version

Version: 0.0.4-alpha

## Contributors

Made with ❤ by [@ochotzas](https://github.com/ochotzas).
