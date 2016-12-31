# pk-to-qr

Utility to export public key pairs as a nice PDF with QR codes.
The generated PDFs can be printed & the key pair recovered with a QR code reader like the following:

- https://github.com/zxing/zxing
- https://play.google.com/store/apps/details?id=com.google.zxing.client.android


## Installation

Clone & run `lein uberjar`

## Usage

    $ java -jar target/uberjar/pk-to-qr-0.1.0-standalone.jar [args]

## Options


| Option | Default | Purpose |
| --- | --- | --- | 
| -p, --private-key filename | $HOME/.ssh/id_rsa | Private key filename |
| -u, --public-key filename | $HOME/.ssh/id_rsa.pub | Public key fielname |
| -o, --output filename | keypair.pdf | Filename for the pdf output |
| -h, --help |  | Show available options |



## License

Copyright © 2016 Cameron Dorrat

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
