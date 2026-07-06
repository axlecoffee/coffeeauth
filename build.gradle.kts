plugins {
    id("coffee.axle.blahaj")
}

blahaj {
    config {}
    setup {
        mocha("0.2.9", include = true)
    }
}

