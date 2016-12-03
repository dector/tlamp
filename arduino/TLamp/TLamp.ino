#define PIN_RED 3
#define PIN_GREEN 5
#define PIN_BLUE 6

void setup() {
  pinMode(PIN_RED, OUTPUT);
  pinMode(PIN_GREEN, OUTPUT);
  pinMode(PIN_BLUE, OUTPUT);
  
  digitalWrite(PIN_RED, LOW);
  digitalWrite(PIN_GREEN, LOW);
  digitalWrite(PIN_BLUE, LOW);
  
  Serial.begin(9600);
}

void loop() {
  if (Serial.available()) {
    String command = Serial.readStringUntil('\n');

    if (processCommand(command)) {
      Serial.println("+" + command);
    } else {
      Serial.println("-" + command);
      Serial.println("UNKNOWN");
    }
  }
}

boolean processCommand(String command) {
  if (command.startsWith("SET #")) {
    String colorStr = command.substring(5, 5+6);

    int buffSize = colorStr.length()+1;
    char buff[buffSize] = {};
    colorStr.toCharArray(buff, buffSize);
    long color = strtol(buff, NULL, 16);

    int r = (color >> 16) & 0xFF;
    int g = (color >> 8) & 0xFF;
    int b = (color >> 0) & 0xFF;

    setColor(r, g, b);

    return true;
  }

  return false;
}

void setColor(int r, int g, int b) {
  analogWrite(PIN_RED, r);
  analogWrite(PIN_GREEN, g);
  analogWrite(PIN_BLUE, b);
}

void blink(int pin) {
  blink(pin, 1);
}

void blink(int pin, int times) {
  for (int i = 0; i < times; i++) {
    if (i != 0) delay(100);
    
    digitalWrite(pin, HIGH);
    delay(100);
    digitalWrite(pin, LOW);
  }
}
