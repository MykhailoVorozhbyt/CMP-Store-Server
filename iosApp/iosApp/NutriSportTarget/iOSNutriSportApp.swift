import SwiftUI
import StoresNutri_sport

@main
struct iOSNutriSportApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
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
