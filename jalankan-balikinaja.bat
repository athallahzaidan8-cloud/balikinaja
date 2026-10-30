@echo off
title BalikinAja - JANGAN TUTUP jendela ini selama aplikasi dipakai
cd /d "%~dp0"

echo ============================================================
echo   BalikinAja - Tubes PBO Kelompok 5
echo   Jendela ini = server aplikasi. Tutup jendela = aplikasi mati.
echo ============================================================
echo.

rem --- cek MySQL XAMPP di port 3307, nyalakan bila belum ---
netstat -ano | findstr /R /C:":3307 .*LISTENING" >nul
if errorlevel 1 (
    if exist "C:\xampp\mysql\bin\mysqld.exe" (
        echo [1/2] MySQL XAMPP belum menyala, menyalakan di port 3307...
        start "" /min "C:\xampp\mysql\bin\mysqld.exe" --defaults-file="C:\xampp\mysql\bin\my.ini"
        timeout /t 5 /nobreak >nul
    ) else (
        echo [!] MySQL XAMPP tidak ditemukan. Nyalakan MySQL dulu lewat XAMPP Control Panel.
        pause
        exit /b 1
    )
) else (
    echo [1/2] MySQL XAMPP sudah menyala di port 3307.
)

rem --- cari Maven: dari PATH, atau bawaan NetBeans ---
set "MVN=mvn"
where mvn >nul 2>nul
if errorlevel 1 (
    for /d %%D in ("C:\Program Files\NetBeans*") do (
        if exist "%%D\netbeans\java\maven\bin\mvn.cmd" set "MVN=%%D\netbeans\java\maven\bin\mvn.cmd"
    )
)

echo [2/2] Menjalankan aplikasi... tunggu sampai muncul "BalikinAja berjalan".
echo       Browser akan terbuka otomatis ke http://localhost:8080
echo.
call "%MVN%" -q spring-boot:run
echo.
echo Aplikasi berhenti. Jika ada pesan error di atas, kirim screenshot-nya.
pause
