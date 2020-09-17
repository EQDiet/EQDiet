@echo off
pushd target\classes
echo Building EQDietBeta0.9.jar...
echo.
if not exist META-INF (mkdir META-INF)
echo Manifest-Version: 1.0>META-INF\MANIFEST.MF
echo Class-Path: .>>META-INF\MANIFEST.MF
echo Main-Class: EQDiet>>META-INF\MANIFEST.MF
if not exist ..\..\Release (mkdir ..\..\Release)
jar cvmf META-INF\MANIFEST.MF ..\..\Release\EQDietBeta0.9.jar *.class ..\..\EQDiet.png
rd /s /q META-INF >NUL 2>NUL
popd
exit /b