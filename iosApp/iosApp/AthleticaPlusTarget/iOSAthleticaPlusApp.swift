import UIKit
import SwiftUI
import StoresAthletica_plus

@main
struct iOSAthleticaPlusApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
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
