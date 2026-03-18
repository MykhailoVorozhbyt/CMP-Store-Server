import SwiftUI
import StoresNutri_sport

@main
struct iOSNutriSportApp: App {
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
        NutriSportMainViewControllerKt.NutriSportMainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

private struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea()
    }
}
