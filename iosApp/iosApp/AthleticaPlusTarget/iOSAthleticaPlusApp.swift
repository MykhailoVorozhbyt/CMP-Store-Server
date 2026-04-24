import UIKit
import SwiftUI
import StoresAthletica_plus
import GoogleSignIn

@main
struct iOSAthleticaPlusApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
    var body: some Scene {
            WindowGroup {
                ContentView()
                    .ignoresSafeArea()
                    .onOpenURL { url in
                        print("Received URL in onOpenURL: \(url)")
                        if GIDSignIn.sharedInstance.handle(url) { return }
                    }
            }
        }
}


private struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        AthleticaPlusMainViewControllerKt.AthleticaPlusMainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

private struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea()
    }
}
