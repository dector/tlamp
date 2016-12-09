#define PIN_RED 3
#define PIN_GREEN 4
#define PIN_BLUE 5

#define PIN_STATUS LED_BUILTIN

#define FIRMWARE_VERSION "tLamp v.0.1"

#define EMPTY_COMMAND_SUCCESS " "
#define EMPTY_COMMAND_FAIL ""

enum LightMode {
  NONE, STATIC, CRUDE, TRIANGLE, CANDLE
};

const int crude_T = 5000;
const int crude_I = 255/2;
const float crude_Ts = 1.0/6 * crude_T;

int currentR;
int currentG;
int currentB;

byte triangleMode = 0;
byte triangleStep = 5;
unsigned int triangleDelay = 50;
unsigned long triangleStepTime;

unsigned long lightModeStart = 0;
LightMode lightMode = NONE;

void setup() {
  pinMode(PIN_RED, OUTPUT);
  pinMode(PIN_GREEN, OUTPUT);
  pinMode(PIN_BLUE, OUTPUT);
  pinMode(PIN_STATUS, OUTPUT);
  
  digitalWrite(PIN_RED, LOW);
  digitalWrite(PIN_GREEN, LOW);
  digitalWrite(PIN_BLUE, LOW);
  digitalWrite(PIN_STATUS, LOW);
  
  Serial.begin(9600);
}

void loop() {
  if (Serial.available()) {
    String command = Serial.readStringUntil('\n');

    String output = processCommand(command);
    if (output.length() > 0) {
      Serial.println("+" + command);
      if (output != EMPTY_COMMAND_SUCCESS)
        Serial.println(output);
      blink(PIN_STATUS, 2);
    } else {
      Serial.println("-" + command);
      Serial.println("UNKNOWN");
      blink(PIN_STATUS, 1);
    }
  }

  processLightMode();
}

void processLightMode() {
  switch(lightMode) {
    case LightMode::CRUDE:
      processCrudeLightMode();
      break;
    case LightMode::TRIANGLE:
      processTriangleLightMode();
      break;
  }
}

void processCrudeLightMode() {
  unsigned long m = millis() - lightModeStart;
  int r = countColor(m, 0*crude_Ts);
  int g = countColor(m, 2*crude_Ts);
  int b = countColor(m, 4*crude_Ts);
  //setColor(1.5*r, g*0.6, b*0.6); // Some calibrations for LED color brightness
  setColor(r, g, b);
}

void processTriangleLightMode() {
  unsigned long ms = millis();
  if (ms < triangleStepTime) {
    return;
  }

  int r = 0;
  int g = 0;
  int b = 0;
  
  switch (triangleMode) {
    case 0: // From RED to GREEN
      r = currentR - triangleStep;
      g = currentG + triangleStep;

      if (r <= 0) r = 0;
      if (g >= 255) g = 255;
      if (g == 255) triangleMode = 1;
      break;
    case 1:
      g = currentG - triangleStep;
      b = currentB + triangleStep;

      if (g <= 0) g = 0;
      if (b >= 255) b = 255;
      if (b == 255) triangleMode = 2;
      break;
    case 2:
      r = currentR + triangleStep;
      b = currentB - triangleStep;

      if (b <= 0) b = 0;
      if (r >= 255) r = 255;
      if (r == 255) triangleMode = 0;
      break;
  }

  setColor(r, g, b);
  
  triangleStepTime = ms + triangleDelay;
}

String processCommand(String command) {
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

    setLightMode(STATIC);

    return EMPTY_COMMAND_SUCCESS;
  } else if (command.equals("CRUDE_LIGHT")) {
    lightModeStart = millis();
    setLightMode(CRUDE);
    return EMPTY_COMMAND_SUCCESS;
  } else if (command.equals("TRIANGLE_CROSS")) {
    lightModeStart = millis();
    setLightMode(TRIANGLE);
    triangleMode = 0;
    return EMPTY_COMMAND_SUCCESS;
  } else if (command.startsWith("VERSION")) {
    return FIRMWARE_VERSION;
  }

  return EMPTY_COMMAND_FAIL;
}

void setLightMode(LightMode mode) {
  lightMode = mode;
}

void setColor(int r, int g, int b) {
  currentR = r;
  currentG = g;
  currentB = b;
  
  analogWrite(PIN_RED, r);
  analogWrite(PIN_GREEN, g / 2);// * 5 / 3);
  analogWrite(PIN_BLUE, b / 2);
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

int countColor(unsigned long millis, int shift) {
  int t = (millis + shift) % crude_T;

  if (0 <= t && t <= 2*crude_Ts) {
    return 0;
  } else if (3*crude_Ts <= t && t <= 5*crude_Ts) {
    return crude_I;
  } else if (2*crude_Ts <= t && t <= 3*crude_Ts) {
    return (int)(crude_I * (t - 2*crude_Ts) / crude_Ts);
  } else if (5*crude_Ts <= t && t <= 6*crude_Ts) {
    return (int)(crude_I - crude_I * (t - 5*crude_Ts) / crude_Ts);
  } else {
    return 0;
  }
}
