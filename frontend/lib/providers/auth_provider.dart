import 'package:flutter/material.dart';
import 'package:firebase_auth/firebase_auth.dart' as firebase_auth;
import 'package:google_sign_in/google_sign_in.dart';

import '../models/user.dart';
import '../services/api_service.dart';

class AuthProvider extends ChangeNotifier {
  final firebase_auth.FirebaseAuth _firebaseAuth = firebase_auth.FirebaseAuth.instance;
  final GoogleSignIn _googleSignIn = GoogleSignIn();
  final ApiService _apiService = ApiService();

  User? _currentUser;
  bool _isLoading = false;
  String? _error;

  User? get currentUser => _currentUser;
  bool get isLoading => _isLoading;
  String? get error => _error;
  bool get isAuthenticated => _currentUser != null && _firebaseAuth.currentUser != null;

  AuthProvider() {
    _initializeAuth();
  }

  void _initializeAuth() {
    _firebaseAuth.authStateChanges().listen((firebase_auth.User? firebaseUser) {
      if (firebaseUser != null) {
        _loadCurrentUser();
      } else {
        _currentUser = null;
        notifyListeners();
      }
    });
  }

  Future<void> _loadCurrentUser() async {
    try {
      _currentUser = await _apiService.getCurrentUser();
      _error = null;
    } catch (e) {
      _error = e.toString();
      print('Error loading current user: $e');
    }
    notifyListeners();
  }

  Future<bool> signInWithEmailAndPassword(String email, String password) async {
    try {
      _setLoading(true);
      _error = null;

      final credential = await _firebaseAuth.signInWithEmailAndPassword(
        email: email,
        password: password,
      );

      if (credential.user != null) {
        await _registerOrUpdateUser();
        return true;
      }
      return false;
    } on firebase_auth.FirebaseAuthException catch (e) {
      _error = _getFirebaseErrorMessage(e);
      return false;
    } catch (e) {
      _error = 'An unexpected error occurred: $e';
      return false;
    } finally {
      _setLoading(false);
    }
  }

  Future<bool> createUserWithEmailAndPassword(String email, String password, String name) async {
    try {
      _setLoading(true);
      _error = null;

      final credential = await _firebaseAuth.createUserWithEmailAndPassword(
        email: email,
        password: password,
      );

      if (credential.user != null) {
        // Update display name in Firebase
        await credential.user!.updateDisplayName(name);
        await _registerOrUpdateUser();
        return true;
      }
      return false;
    } on firebase_auth.FirebaseAuthException catch (e) {
      _error = _getFirebaseErrorMessage(e);
      return false;
    } catch (e) {
      _error = 'An unexpected error occurred: $e';
      return false;
    } finally {
      _setLoading(false);
    }
  }

  Future<bool> signInWithGoogle() async {
    try {
      _setLoading(true);
      _error = null;

      final GoogleSignInAccount? googleUser = await _googleSignIn.signIn();
      if (googleUser == null) {
        return false; // User cancelled
      }

      final GoogleSignInAuthentication googleAuth = await googleUser.authentication;
      final credential = firebase_auth.GoogleAuthProvider.credential(
        accessToken: googleAuth.accessToken,
        idToken: googleAuth.idToken,
      );

      final userCredential = await _firebaseAuth.signInWithCredential(credential);
      if (userCredential.user != null) {
        await _registerOrUpdateUser();
        return true;
      }
      return false;
    } on firebase_auth.FirebaseAuthException catch (e) {
      _error = _getFirebaseErrorMessage(e);
      return false;
    } catch (e) {
      _error = 'An unexpected error occurred: $e';
      return false;
    } finally {
      _setLoading(false);
    }
  }

  Future<void> _registerOrUpdateUser() async {
    try {
      _currentUser = await _apiService.registerUser();
    } catch (e) {
      print('Error registering/updating user: $e');
      // Try to load existing user
      await _loadCurrentUser();
    }
  }

  Future<void> signOut() async {
    try {
      _setLoading(true);
      await _firebaseAuth.signOut();
      await _googleSignIn.signOut();
      _currentUser = null;
      _error = null;
    } catch (e) {
      _error = 'Error signing out: $e';
    } finally {
      _setLoading(false);
    }
  }

  Future<bool> updateProfile({String? name, String? profilePictureUrl}) async {
    try {
      _setLoading(true);
      _error = null;

      // Update Firebase profile
      final firebaseUser = _firebaseAuth.currentUser;
      if (firebaseUser != null && name != null) {
        await firebaseUser.updateDisplayName(name);
      }

      // Update backend profile
      _currentUser = await _apiService.updateProfile(
        name: name,
        profilePictureUrl: profilePictureUrl,
      );

      return true;
    } catch (e) {
      _error = 'Error updating profile: $e';
      return false;
    } finally {
      _setLoading(false);
    }
  }

  Future<bool> sendPasswordResetEmail(String email) async {
    try {
      _setLoading(true);
      _error = null;

      await _firebaseAuth.sendPasswordResetEmail(email: email);
      return true;
    } on firebase_auth.FirebaseAuthException catch (e) {
      _error = _getFirebaseErrorMessage(e);
      return false;
    } catch (e) {
      _error = 'An unexpected error occurred: $e';
      return false;
    } finally {
      _setLoading(false);
    }
  }

  void clearError() {
    _error = null;
    notifyListeners();
  }

  void _setLoading(bool loading) {
    _isLoading = loading;
    notifyListeners();
  }

  String _getFirebaseErrorMessage(firebase_auth.FirebaseAuthException e) {
    switch (e.code) {
      case 'user-not-found':
        return 'No user found with this email address.';
      case 'wrong-password':
        return 'Incorrect password.';
      case 'email-already-in-use':
        return 'An account already exists with this email address.';
      case 'weak-password':
        return 'Password is too weak.';
      case 'invalid-email':
        return 'Invalid email address.';
      case 'user-disabled':
        return 'This account has been disabled.';
      case 'too-many-requests':
        return 'Too many failed attempts. Please try again later.';
      case 'operation-not-allowed':
        return 'This sign-in method is not enabled.';
      default:
        return e.message ?? 'An authentication error occurred.';
    }
  }
} 