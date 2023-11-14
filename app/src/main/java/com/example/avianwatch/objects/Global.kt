package com.example.avianwatch.objects

import android.location.Location
import com.example.avianwatch.R
import com.example.avianwatch.data.BirdFact
import com.example.avianwatch.data.BirdObservation
import com.example.avianwatch.data.Hotspot
import com.example.avianwatch.data.HotspotWithMarker
import com.example.avianwatch.data.Post
import com.example.avianwatch.data.RouteData
import com.example.avianwatch.data.StepData
import com.example.avianwatch.data.User
import com.example.avianwatch.data.UserPreferences
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth

//Singleton class for storing live data
object Global {
    var currentUser: User? = null
    var userPreferences: UserPreferences? = null
    var hotspotsData: MutableList<Hotspot> = mutableListOf()
    var routeData: RouteData? = null
    var stepDataList: MutableList<StepData> = mutableListOf()
    var users: MutableList<User> = mutableListOf()
    var posts: MutableList<Post> = mutableListOf()
    var observations: MutableList<BirdObservation> = mutableListOf()
    var hotspots: MutableList<Hotspot> = mutableListOf()
    var hotspotsWithMarker: MutableList<HotspotWithMarker> = mutableListOf()
    lateinit var currentLocation: Location
    lateinit var liveLocation: MarkerOptions
    lateinit var selectedBirdName: String


    //list of bird facts with corresponding image resource IDs
    val birdFactsList = listOf(
        BirdFact(
            "In the continental U.S. alone, between 1.4 billion and 3.7 billion birds are killed by cats annually.",
            R.mipmap.bird_predator
        ),
        BirdFact(
            "Though it looks like a bird’s knee is bending backwards, what is bending backward is actually its ankle. Below its ankle is an extended foot bone, leading to the toes. A bird’s real knee is usually hidden by feathers.",
            R.mipmap.bird_legs
        ),
        BirdFact(
            "Flamingos pair for a lifetime. Some stay with their mates for 50 years or more.",
            R.mipmap.flamingo_pair
        ),
        BirdFact(
            "The song of a European wren is made of more than 700 different notes a minute and can be heard 1,650 feet (500 m) away.",
            R.mipmap.wren_bird_song
        ),
        BirdFact(
            "The Sooty Tern spends more time in the air than any other bird. It takes off over the ocean and flies for at least 3 years without settling on water or land. Swifts also spend most of their lives in the air. They can even sleep in the air by gliding on air currents with their wings outstretched.",
            R.mipmap.sooty_tern_flight
        ),
        BirdFact(
            "A group of crows is called a murder or congress. A group of owls is called a parliament, wisdom, or study. A group of flamingos is called a flamboyance.",
            R.mipmap.group_of_crows
        ),
        BirdFact(
            "The Fieldfare birds have a special way to attack an enemy bird. They gang up on it and make it fly to the ground. Then the Fieldfares fly into the air and drop poop on the bird.",
            R.mipmap.fieldfare_poop
        ),
        BirdFact(
            "Vultures have stomach acid so corrosive that they can digest carcasses infected with anthrax.",
            R.mipmap.eating_vulture
        ),
        BirdFact(
            "The slowest flying bird is the American Woodcock. It can fly at just 5 mph (8 kph). When hummingbirds hover, they move at 0 mph. Additionally, hummingbirds are the only birds that can fly backwards under power, registering a negative speed.",
            R.mipmap.wood_cock
        ),
        BirdFact(
            "The fastest level flight by a bird has been seen in both the Spine-tailed Swift and the Red-breasted Merganser (a duck). They have flown at 100 mph (161 kph) in level flight.",
            R.mipmap.red_breasted_merganser
        ),
        BirdFact(
            "The heaviest bird in the air is the Kori Bustard, from East and South Africa. It weighs about 31 lb. (14 kg.), with the largest on recorded being 40 lb. (18 kg.). Because it is such hard work to fly, it flies only in emergencies and for only short distances.",
            R.mipmap.kori_bustard
        ),
        BirdFact(
            "The fastest flying bird in a dive is the Peregrine Falcon. It averages speeds of over 110 mph (180 kph).",
            R.mipmap.peregrine_falcon
        )
    )


    //list of bird species
    val birdSpecies = listOf(
        "Sparrow", "Robin", "Cardinal", "Blue Jay", "Hummingbird",
        "Eagle", "Hawk", "Owl", "Pelican", "Pigeon",
        "Crow", "Finch", "Woodpecker", "Seagull", "Falcon",
        "Swallow", "Albatross", "Kingfisher", "Parrot", "Toucan",
        "Penguin", "Heron", "Warbler", "Osprey", "Quail",
        "Wren", "Stork", "Egret", "Nuthatch", "Grosbeak",
        "Black-capped Chickadee", "American Goldfinch", "Northern Flicker", "Mallard", "Bald Eagle",
        "Common Loon", "White-crowned Sparrow", "Eastern Bluebird", "Western Bluebird", "Northern Mockingbird",
        "Scarlet Tanager", "Northern Saw-whet Owl", "Black-throated Sparrow", "Black-crowned Night-Heron", "Ruby-crowned Kinglet",
        "Golden-crowned Kinglet", "Mountain Bluebird", "Black-capped Vireo", "White-eyed Vireo", "Yellow Warbler",
        "Hooded Warbler", "American Redstart", "Black-and-white Warbler", "Yellow-rumped Warbler", "Cerulean Warbler",
        "Black-throated Blue Warbler", "Prothonotary Warbler", "Louisiana Waterthrush", "Northern Waterthrush", "Common Yellowthroat",
        "Chestnut-sided Warbler", "Blackpoll Warbler", "Bay-breasted Warbler", "Blackburnian Warbler", "Palm Warbler",
        "Pine Warbler", "Yellow-throated Warbler", "Prairie Warbler", "Black-throated Green Warbler", "Canada Warbler",
        "Wilson's Warbler", "American Tree Sparrow", "Chipping Sparrow", "Clay-colored Sparrow", "Lark Sparrow", "Lark Bunting",
        "White-crowned Sparrow", "White-throated Sparrow", "Harris's Sparrow", "LeConte's Sparrow", "Savannah Sparrow",
        "Song Sparrow", "Lincoln's Sparrow", "Swamp Sparrow", "Eastern Towhee", "Spotted Towhee", "American Tree Sparrow",
        "Chipping Sparrow", "Clay-colored Sparrow", "Lark Sparrow", "Lark Bunting", "White-crowned Sparrow", "White-throated Sparrow",
        "Harris's Sparrow", "LeConte's Sparrow", "Savannah Sparrow", "Song Sparrow", "Lincoln's Sparrow", "Swamp Sparrow",
        "Eastern Towhee", "Spotted Towhee"
    )


    //eBird API key
    var eBirdApiKey = "e7nbn16ihfum"

    //google maps API key
    var googleMapsApiKey = "AIzaSyAv9rGpt3jZ3DlHL5JUdiXzG5QfQCcreFE"
}
