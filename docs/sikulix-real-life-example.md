# SikuliX + Selenium Gerçek Hayat Örneği (Native Dosya Yükleme Penceresi)

Önce hızlı ve risksiz bir giriş yapmak isterseniz, [docs/sikulix-quick-demo.md](/Users/hakan.yaman/Projects/test-automation-with-selenium-webdriver-for-java-2124033/docs/sikulix-quick-demo.md) dosyasındaki self-contained demoyu çalıştırın. Bu dosyadaki örnek ise gerçek hayatta daha sık karşılaşılan native dosya yükleme akışını gösterir.

Bu örnek, Selenium'un web tarafını kontrol ettiği; SikuliX'in ise işletim sistemi seviyesindeki **native dosya seçme penceresini** yönettiği hibrit bir senaryodur.

## 1) Bağımlılıkları ekle
`pom.xml` içerisine `sikulixapi` bağımlılığını ekleyin.

## 2) Görsel şablonları hazırla
Aşağıdaki iki resmi kendi işletim sistemi temanıza göre ekran görüntüsü alarak üretin:

- `src/test/resources/images/file_name_input.png`  
  (Dosya adı girilen input alanı)
- `src/test/resources/images/open_button.png`  
  (Aç/Open butonu)

> Önemli: SikuliX görsel eşleştirme yaptığı için çözünürlük, ölçeklendirme (125/150%), tema ve dil farkları eşleşmeyi etkiler.

## 3) Testi çalıştır
Varsayılan olarak test `@Disabled` işaretli (CI/CD'de kırılmaması için).
Elle çalıştırmak için `@Disabled` annotation'ını kaldırın ve şu komutu çalıştırın:

```bash
mvn -Dtest=SikuliXNativeFileUploadTest test
```

## 4) Neden bu yaklaşım gerçek hayatta kullanılır?
- Selenium doğrudan native diyalogları kontrol edemez.
- Kurumsal uygulamalarda upload akışları bazen native pencere açar.
- Bu durumlarda Selenium + SikuliX kombinasyonu pratik ve sürdürülebilir bir çözümdür.
