# SikuliX Hızlı Demo

Bu demo, ekranda görünen bir pencereyi SikuliX ile gerçek piksel eşleştirmesi yaparak kontrol eder.

## Ne gösteriyor?
- SikuliX ekrandaki turuncu `START DEMO` butonunu bulur.
- Butona tıklar.
- Ekranda `CLICK CONFIRMED` rozeti görünür.

Bu örnek dış siteye, dosya yükleme penceresine veya manuel ekran görüntüsü toplamaya ihtiyaç duymaz.

## Nasıl çalıştırılır?
IDE içinden en hızlı yol:

- `examples.SikuliXQuickDemo` sınıfındaki `main()` metodunu çalıştırın.

JUnit ile çalıştırmak isterseniz:

- `examples.SikuliXQuickDemoTest` testini çalıştırın.

Maven kuruluysa:

```bash
mvn -Dtest=SikuliXQuickDemoTest test
```

## Demo sırasında dikkat edilmesi gerekenler
- Makinede görünür bir masaüstü oturumu açık olmalı.
- Pencere minimize edilmemeli.
- macOS kullanıyorsanız IDE/terminal için `Screen Recording` ve gerekirse `Accessibility` izni gerekebilir.

macOS'ta izin vermek için:

- `System Settings > Privacy & Security > Screen Recording` içine girin ve kullandığınız IDE/terminali etkinleştirin.
- `System Settings > Privacy & Security > Accessibility` içine girin ve aynı uygulamayı etkinleştirin.
- İzinlerden sonra IDE veya terminali tamamen kapatıp yeniden açın.

## Eğitimde nasıl anlatılır?
- Selenium DOM tabanlı çalışır; native veya piksel tabanlı alanlarda sınırı vardır.
- SikuliX doğrudan ekrandaki görüntüyü hedef alır.
- Bu hızlı demo, "görüntü tabanlı otomasyon gerçekten çalışıyor mu?" sorusuna 10 saniyelik net bir cevap verir.
- Sonraki adım olarak [docs/sikulix-real-life-example.md](/Users/hakan.yaman/Projects/test-automation-with-selenium-webdriver-for-java-2124033/docs/sikulix-real-life-example.md) dosyasındaki native upload örneğine geçebilirsiniz.
