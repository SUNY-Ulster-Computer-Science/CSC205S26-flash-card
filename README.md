
What UI and Editor we will use:


1. For Design: Figma
Before we touch a single line of code, Figma is the industry standard. It allows you to prototype the layout, choose your color palette, and test the flow of the app.



2. For Development: Visual Studio Code (VS Code)
While not a drag-and-drop editor, VS Code is where the actual UI is built using code.



3. The  Alternative: v0.dev or Lovable
If we want to generate a UI layout using AI and then tweak it, these tools allow you to describe your UI in plain English and export the code.


Overview:

The Flip Mechanic: A clean UI interaction where the user clicks to reveal the answer.

Progress Tracking: Simple Got it vs. Need more practice buttons to sort cards.

Data Persistence: Saving cards so they don’t vanish when you refresh the page.


Method:

Python + Flet 

void show(page)
Description: Initializes the application window and sets the initial theme and layout.

Parameters: * page : The Flet Page object representing the application window.

Returns: Nothing

String get_text(control)
Description: Retrieves the current value from a specific UI element (like a text input).

Parameters: * control : The Flet control object (e.g., ft.TextField).

Returns: The text value as a String.

void set_text(control, text)
Description: Updates the display text of a label or the value of an input field.

Parameters:

control : The Flet control to update.

text : String to set as the new value.

Returns: Nothing

void clear_text(control)
Description: Resets a text field or label to an empty state.

Parameters: * control : The Flet control to clear.

Returns: Nothing

void add_button(page, label, action)
Description: Dynamically adds an elevated button to the page.

Parameters:

page : The active Flet Page.

label : The text displayed on the button (String).

action : The Python function to trigger when clicked (Callable).

Returns: Nothing

void alert(page, message)
Description: Shows a temporary "SnackBar" notification at the bottom of the screen.

Parameters:

page : The active Flet Page.

message : The notification text (String).

Returns: Nothing

void flip_card(card)
Description: Toggles the rotation of a card container to simulate a flip.

Parameters: * card : The ft.Container acting as the flashcard.

Returns: Nothing
