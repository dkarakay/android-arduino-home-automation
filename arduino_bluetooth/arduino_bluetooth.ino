const int ROLE_PIN = 5;  // Röleyi kontrol etmek için kullandığımız pin

// Arduino çalıştığında bir defaya mahsus çalışacak kodlar
void setup() {
  // Röleyi kontrol edebilmek için pinimizi OUTPUT moduna alıyoruz
  pinMode(ROLE_PIN, OUTPUT);

  // Serial Monitor üzerinden veri göndereceğimiz için baundrate'i 9600 olan Serial iletişimi başlatıyoruz
  Serial.begin(9600);

}

// Arduino içerisinde döngü şeklinde çalışan fonksiyonumuz
void loop() {
  while (Serial.available() > 0 ) {
    char data = Serial.read();
    switch (data) {

      // Eğer Serial iletişimden 0 karakterini alırsak Rölemizi kapatıp oradan geçen elektriği kesecek, böylece lambamız sönecek.
      case '0':
        // Röle pinini dijital olarak kapatıyoruz yani o pin artık bir gerilim vermiyor.
        digitalWrite(ROLE_PIN, LOW);
        break;
        
      // Eğer Serial iletişimden 1 karakterini alırsak Rölemizi açıp oradan elektrik geçmesini sağlayacak böylece lambamız yanacak.
      case '1':
        // Röle pinini dijital olarak açıyoruz yani o pin 5V gerilim veriyor.
        digitalWrite(ROLE_PIN, HIGH);
        break;

      default:
        break;

    }

  }
}
