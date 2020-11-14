// Firebase App (the core Firebase SDK) is always required and must be listed first
import firebase from "firebase/app";
// Add the Firebase products that you want to use
import "firebase/auth";
import "firebase/firestore";

// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: "AIzaSyAjP4pwHtpKdeVp90ZxBTSPSAA93K7hfxk",
  authDomain: "project-iii-9598c.firebaseapp.com",
  databaseURL: "https://project-iii-9598c.firebaseio.com",
  projectId: "project-iii-9598c",
  storageBucket: "project-iii-9598c.appspot.com",
  messagingSenderId: "101824099469",
  appId: "1:101824099469:web:d0004748ff5dc94b739ec4",
  measurementId: "G-7ZJ6NH6YKT",
};
firebase.initializeApp(firebaseConfig);

export const firebaseAuth = firebase.auth();

export const firestore = firebase.firestore();

export default firebase;
