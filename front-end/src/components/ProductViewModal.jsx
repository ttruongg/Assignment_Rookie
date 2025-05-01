import {
  Button,
  Dialog,
  DialogBackdrop,
  DialogPanel,
  DialogTitle,
} from "@headlessui/react";
import { Divider } from "@mui/material";
import Status from "./Status";
import { MdClose, MdDone } from "react-icons/md";
function ProductViewModal({ open, setOpen, product, isAvailable }) {
  const {
    productId,
    productName,
    images,
    quantity,
    brand,
    description,
    price,
    discount,
    specialPrice,
  } = product;

  const productImage = images && images.length > 0 ? images[1].imageUrl : "";

  const handleClickOpen = () => {
    setOpen(true);
  };

  return (
    <>
      <Dialog
        open={open}
        as="div"
        className="relative z-10 focus:outline-none"
        onClose={() => setOpen(false)}
      >
        <DialogBackdrop className="fixed inset-0 bg-black/50" />
        <div className="fixed inset-0 z-10 w-screen overflow-y-auto">
          <div className="flex min-h-full items-center justify-center p-4">
            <DialogPanel
              transition
              className="w-full max-w-md rounded-xl bg-white/5 p-6 backdrop-blur-2xl duration-300 ease-out data-closed:transform-[scale(95%)] data-closed:opacity-0"
            >
              {images && (
                <div className="flex justify-center aspect-[3/2]">
                  <img src={productImage} alt={productName} />
                </div>
              )}

              <div className="px-6 pt-10 pb-2">
                <DialogTitle
                  as="h1"
                  className="lg:text-3xl sm:text-2xl text-xl font-semibold leading-6 text-white mb-4"
                >
                  {productName}
                </DialogTitle>

                <div className="space-y-2 text-gray-700 pb-4">
                  <div className="flex items-center justify-between gap-2">
                    {specialPrice ? (
                      <div className="flex items-center gap-2">
                        <span className="text-stone-300 line-through">
                          ${Number(price).toFixed(2)}
                        </span>
                        <span className="sm:text-xl font-semibold text-stone-100">
                          ${Number(specialPrice).toFixed(2)}
                        </span>
                      </div>
                    ) : (
                      <span className="text-xl font-bold">
                        {" "}
                        ${Number(price).toFixed(2)}
                      </span>
                    )}

                    {isAvailable ? (
                      <Status
                        text="In Stock"
                        icon={MdDone}
                        bg="bg-teal-200"
                        color="text-teal-900"
                      />
                    ) : (
                      <Status
                        text="Out of Stock"
                        icon={MdClose}
                        bg="bg-rose-200"
                        color="text-rose-900"
                      />
                    )}
                  </div>
                </div>
                <Divider className="bg-white" />

                <p className="mt-2 text-sm/6 text-violet-50">{description}</p>
                <div className="mt-4">
                  <Button
                    className="rounded-md bg-gray-700 px-3 py-1.5 text-sm/6 font-semibold text-white"
                    onClick={() => setOpen(false)}
                  >
                    Close
                  </Button>
                </div>
              </div>
            </DialogPanel>
          </div>
        </div>
      </Dialog>
    </>
  );
}

export default ProductViewModal;
