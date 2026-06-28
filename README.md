# TrustLens
# 🔍 TrustLens

## AI-Powered Document Authenticity Verification Platform

> **"Making digital documents trustworthy in an AI-driven world."**

TrustLens is an AI-powered Android application that helps users verify whether a digital document is genuine or modified.

With the rise of AI-generated content and easy document editing tools, identifying fake or altered documents has become difficult. TrustLens simplifies this process by analyzing documents, discovering trusted sources, comparing content, and generating an explainable authenticity score.

---

# 🚨 The Problem

Digital documents are everywhere:

* Company reports
* Certificates
* Financial documents
* Official announcements

But today:

❌ Documents can be edited easily
❌ Fake versions can look real
❌ Manual verification takes time
❌ Users don't know what to trust

There is a need for a simple system that can verify digital trust.

---

# 💡 Our Solution

TrustLens works as a digital verification assistant.

The user uploads a document → TrustLens analyzes it → finds trusted references → compares information → generates a verification result.

Instead of only saying "real or fake", TrustLens explains **why** a document is trusted or suspicious.

---

# ⚙️ How TrustLens Works

```
📄 Upload Document
          |
          ↓
🔎 OCR + Metadata Extraction
          |
          ↓
🌐 Official Source Discovery
          |
          ↓
⚖️ Document Comparison
          |
          ↓
🤖 AI Investigation
          |
          ↓
📊 Trust Score & Verification Report
```

---

# ✨ Key Features

## 📄 Smart Document Upload

* Upload PDF and image documents
* Process documents for verification

## 🔤 OCR & Metadata Analysis

* Extract text from documents
* Analyze document information
* Identify important details

## 🌐 Official Source Verification

* Identify document issuer
* Find trusted official sources
* Compare with original references

## ⚖️ Document Comparison

* Compare uploaded vs trusted documents
* Detect content differences
* Highlight inconsistencies

## 🤖 AI Investigator

* Analyze verification results
* Generate explainable insights
* Provide authenticity assessment

## 📊 Trust Dashboard

* Trust Score
* Risk Level
* Verification Summary
* Detected issues

---

# 🏗️ System Architecture

```
              Android Application
          (Kotlin + Jetpack Compose)

                    |
                    ↓

              FastAPI Backend

                    |
        ┌───────────┼───────────┐
        ↓           ↓           ↓

      OCR     Source Check     AI Analysis

        ↓           ↓           ↓

          Verification Result
```

---

# 🛠️ Tech Stack

## 📱 Android

* Kotlin
* Android Studio
* Jetpack Compose
* Material 3
* MVVM Architecture

## ⚙️ Backend

* Python
* FastAPI

## 🧠 AI & Processing

* Gemini API
* Tesseract OCR
* PyMuPDF

## 🔎 Verification Engine

* BeautifulSoup
* Requests
* RapidFuzz

## 🗄️ Database

* MongoDB Atlas

## 🚀 Deployment

* GitHub
* Render

---

# 🎯 Example Workflow

User uploads:

```
Tata Motors Annual Report.pdf
```

TrustLens:

✅ Extracts document information
✅ Identifies official source
✅ Compares both versions
✅ Detects inconsistencies
✅ Generates trust analysis

Example Result:

```
Trust Score: 92/100

Risk Level: Low

Reason:
✓ Official source matched
✓ Content similarity high
✓ No major modifications detected
```

---

# 🌱 Future Scope

Future improvements:

* 🖼️ Image authenticity verification
* 📰 News verification
* 📚 Research paper verification
* 🏢 Enterprise document verification
* 🔐 Advanced verification records

---

# 👨‍💻 Team DevFusion

Built for HackVerse 2026

Team Members:

* Aditya Pareek
* Renuka
* Riya
* Alok

---

# 🤝 Why TrustLens?

Because in a world where creating fake content is becoming easier,

**verifying what is real should become easier too.**
